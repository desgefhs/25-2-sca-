
package org.newdawn.spaceinvaders;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.ShotEntity;
import org.newdawn.spaceinvaders.view.MainMenu;
import org.newdawn.spaceinvaders.view.PauseMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class GameManager implements GameContext {

    private final InputHandler inputHandler;
    private final EntityManager entityManager;
    private final CollisionDetector collisionDetector;
    private final GameWindow gameWindow;
    private final MainMenu mainMenu;
    private final PauseMenu pauseMenu;
    private final Firestore db;
    private final AuthenticatedUser user;

    private GameState currentState;
    private boolean gameRunning = true;
    private boolean logicRequiredThisLoop = false;
    private String message = "";
    private long lastFire = 0;
    private final long firingInterval = 50;
    private final double moveSpeed = 300;
    private static final int SHOT_DAMAGE = 1;
    private static final int ALIEN_SCORE = 10;
    private int score = 0;
    private int wave = 1;
    private int lineCount = 0;
    private long lastLineSpawnTime = 0;
    private final long lineSpawnInterval = 3000; // 3 seconds
    private final int LINES_PER_WAVE = 10;

    private List<String> highScores = new ArrayList<>();

    public GameManager(AuthenticatedUser user, Firestore db) {
        this.user = user;
        this.db = db;
        this.inputHandler = new InputHandler();
        this.entityManager = new EntityManager(this);
        this.collisionDetector = new CollisionDetector();
        this.gameWindow = new GameWindow(inputHandler);
        this.mainMenu = new MainMenu();
        this.pauseMenu = new PauseMenu();
        setCurrentState(GameState.MAIN_MENU);
    }

    public void startGame() {
        gameWindow.setVisible(true);
        mainLoop();
    }

    private void setCurrentState(GameState newState) {
        this.currentState = newState;
    }

    private void mainLoop() {
        long lastLoopTime = SystemTimer.getTime();

        while (gameRunning) {
            long delta = SystemTimer.getTime() - lastLoopTime;
            lastLoopTime = SystemTimer.getTime();

            switch (currentState) {
                case MAIN_MENU:
                    handleMenuInput();
                    gameWindow.getGameCanvas().renderMenu(mainMenu);
                    break;
                case RANKING:
                    if (inputHandler.isFirePressedAndConsume()) {
                        setCurrentState(GameState.MAIN_MENU);
                    }
                    gameWindow.getGameCanvas().renderRanking(highScores);
                    break;
                case PLAYING:
                    updateGame(delta);
                    break;
                case PAUSED:
                    handlePauseMenuInput();
                    gameWindow.getGameCanvas().render(entityManager.getEntities(), message, score, currentState, 0, wave, pauseMenu);
                    break;
                case GAME_OVER:
                case GAME_WON:
                    if (inputHandler.isFirePressedAndConsume()) {
                        setCurrentState(GameState.MAIN_MENU);
                    }
                    gameWindow.getGameCanvas().render(entityManager.getEntities(), message, score, currentState, 0, wave, pauseMenu);
                    break;
            }
            SystemTimer.sleep(lastLoopTime + 10 - SystemTimer.getTime());
        }
    }

    private void updateGame(long delta) {
        if (inputHandler.isPPressedAndConsume()) {
            setCurrentState(GameState.PAUSED);
            return;
        }
        handlePlayingInput(delta);

        // Spawn aliens
        if (wave % 5 == 0) { // Boss Wave
            if (lineCount == 0) {
                entityManager.spawnNext(wave, lineCount);
                lineCount++; // Increment to prevent re-spawning
            }
        } else { // Normal Wave
            if (lineCount < LINES_PER_WAVE) {
                if (System.currentTimeMillis() - lastLineSpawnTime > lineSpawnInterval) {
                    entityManager.spawnNext(wave, lineCount);
                    lineCount++;
                    lastLineSpawnTime = System.currentTimeMillis();
                }
            }
        }

        entityManager.moveAll(delta);
        collisionDetector.checkCollisions(entityManager.getEntities());
        entityManager.cleanup();
        if (logicRequiredThisLoop) {
            entityManager.doLogicAll();
            logicRequiredThisLoop = false;
        }
        gameWindow.getGameCanvas().render(entityManager.getEntities(), message, score, currentState, 0, wave, pauseMenu);
    }

    private void handleMenuInput() {
        if (inputHandler.isLeftPressedAndConsume()) mainMenu.moveLeft();
        if (inputHandler.isRightPressedAndConsume()) mainMenu.moveRight();

        if (inputHandler.isFirePressedAndConsume()) {
            String selected = mainMenu.getSelectedItem();
            if ("1. 게임시작".equals(selected)) {
                startGameplay();
            } else if ("2. 랭킹".equals(selected)) {
                getHighScores();
                setCurrentState(GameState.RANKING);
            } else if ("5. 설정".equals(selected)) {
                System.exit(0);
            }
        }
    }
    
    private void handlePauseMenuInput() {
        if (inputHandler.isUpPressedAndConsume()) pauseMenu.moveUp();
        if (inputHandler.isDownPressedAndConsume()) pauseMenu.moveDown();
        if (inputHandler.isFirePressedAndConsume()) {
            String selected = pauseMenu.getSelectedItem();
            if ("재개하기".equals(selected)) setCurrentState(GameState.PLAYING);
            else if ("메인메뉴로 나가기".equals(selected)) setCurrentState(GameState.MAIN_MENU);
            else if ("종료하기".equals(selected)) System.exit(0);
        }
    }

    private void startGameplay() {
        resetScore();
        wave = 1;
        lineCount = 0;
        lastLineSpawnTime = System.currentTimeMillis();
        entityManager.initShip();
        setCurrentState(GameState.PLAYING);
        message = "";
    }

    private void handlePlayingInput(long delta) {
        ShipEntity ship = entityManager.getShip();
        ship.setHorizontalMovement(0);
        ship.setVerticalMovement(0);

        if (inputHandler.isLeftPressed() && !inputHandler.isRightPressed()) ship.setHorizontalMovement(-moveSpeed);
        else if (inputHandler.isRightPressed() && !inputHandler.isLeftPressed()) ship.setHorizontalMovement(moveSpeed);

        if (inputHandler.isUpPressed() && !inputHandler.isDownPressed()) ship.setVerticalMovement(-moveSpeed);
        if (inputHandler.isDownPressed() && !inputHandler.isUpPressed()) ship.setVerticalMovement(moveSpeed);

        if (inputHandler.isFirePressed()) tryToFire();
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) return;
        lastFire = System.currentTimeMillis();
        ShipEntity ship = entityManager.getShip();
        ShotEntity shot = new ShotEntity(this, "sprites/shot.gif", ship.getX() + 10, ship.getY() - 30, SHOT_DAMAGE);
        entityManager.addEntity(shot);
    }

    @Override
    public void notifyDeath() {
        saveScore();
        message = "Oh no! They got you, try again?";
        setCurrentState(GameState.GAME_OVER);
    }

    @Override
    public void notifyWin() {
        saveScore();
        message = "Well done! You Win!";
        setCurrentState(GameState.GAME_WON);
    }

    private void saveScore() {
        if (user.getLocalId() == null || user.getLocalId().trim().isEmpty() || db == null) return;

        DocumentReference userHighScoreRef = db.collection("scores").document(user.getLocalId());

        ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = userHighScoreRef.get();
        try {
            com.google.cloud.firestore.DocumentSnapshot document = future.get(); // Get existing high score

            long existingHighScore = 0;
            if (document.exists()) {
                Long scoreLong = document.getLong("score");
                if (scoreLong != null) {
                    existingHighScore = scoreLong;
                }
            }

            if (score > existingHighScore) { // Only save if current score is higher
                Map<String, Object> scoreData = new HashMap<>();
                scoreData.put("uid", user.getLocalId());
                scoreData.put("username", user.getUsername());
                scoreData.put("score", score);
                scoreData.put("timestamp", System.currentTimeMillis()); // Update timestamp as well

                userHighScoreRef.set(scoreData); // Overwrite with new high score
                System.out.println("New high score saved for " + user.getUsername() + ": " + score);
            } else {
                System.out.println("Current score " + score + " is not higher than existing high score " + existingHighScore + " for " + user.getUsername());
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getHighScores() {
        if (db == null) return;
        highScores.clear();
        ApiFuture<QuerySnapshot> query = db.collection("scores").orderBy("score", com.google.cloud.firestore.Query.Direction.DESCENDING).limit(10).get();
        try {
            QuerySnapshot querySnapshot = query.get();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                highScores.add(document.getString("username") + ": " + document.getLong("score"));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void addEntity(Entity entity) { entityManager.addEntity(entity); }
    @Override
    public void removeEntity(Entity entity) { entityManager.removeEntity(entity); }
    @Override
    public void notifyAlienKilled() { 
        increaseScore(ALIEN_SCORE);
        entityManager.decreaseAlienCount();
        if (entityManager.getAlienCount() == 0 && lineCount >= LINES_PER_WAVE) {
            setCurrentState(GameState.WAVE_CLEARED);
        }
    }
    @Override
    public void updateLogic() { logicRequiredThisLoop = true; }
    @Override
    public void notifyAlienEscaped(Entity entity) {
        entityManager.removeEntity(entity);
        entityManager.decreaseAlienCount();
        if (entityManager.getAlienCount() == 0 && lineCount >= LINES_PER_WAVE) {
            setCurrentState(GameState.WAVE_CLEARED);
        }
    }
    public void increaseScore(int amount) { score += amount; }
    public void resetScore() { score = 0; }
    public int getScore() { return score; }
     public void startNextWave() {
        wave++;
        lineCount = 0;
        lastLineSpawnTime = System.currentTimeMillis();
        message = "Wave " + wave;
        entityManager.initShip();
        setCurrentState(GameState.PLAYING);
    }
}
