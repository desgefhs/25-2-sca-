package org.newdawn.spaceinvaders.core;

import com.google.cloud.firestore.Firestore;
import org.newdawn.spaceinvaders.auth.AuthenticatedUser;
import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.view.GameWindow;
import org.newdawn.spaceinvaders.view.MainMenu;
import org.newdawn.spaceinvaders.view.PauseMenu;

/**
 * 게임의 핵심 로직과 상태를 관리하며, 다른 전문 관리 클래스들을 총괄하는 메인 컨트롤러.
 */
public class GameManager implements GameContext {

    private final InputHandler inputHandler;
    private final EntityManager entityManager;
    private final CollisionDetector collisionDetector;
    private final GameWindow gameWindow;
    private final MainMenu mainMenu;
    private final PauseMenu pauseMenu;
    private final DatabaseManager databaseManager;
    private final AuthenticatedUser user;

    private PlayerData currentPlayer;

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
    private final long lineSpawnInterval = 3000;
    private final int LINES_PER_WAVE = 10;

    private long lastMeteorSpawnTime = 0;
    private final long meteorSpawnInterval = 3000; // 3 seconds
    private long lastBombSpawnTime = 0;
    private final long bombSpawnInterval = 5000; // 5 seconds

    public GameManager(AuthenticatedUser user, Firestore db) {
        this.user = user;
        this.databaseManager = new DatabaseManager(db);
        this.inputHandler = new InputHandler();
        this.entityManager = new EntityManager(this);
        this.collisionDetector = new CollisionDetector();
        this.gameWindow = new GameWindow(inputHandler);
        this.mainMenu = new MainMenu();
        this.pauseMenu = new PauseMenu();
        setCurrentState(GameState.MAIN_MENU);
    }

    public void initializePlayer() {
        this.currentPlayer = databaseManager.loadPlayerData(user.getLocalId());
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
                    // 랭킹 렌더링 시 DB매니저로부터 직접 데이터를 가져와 전달
                    gameWindow.getGameCanvas().renderRanking(databaseManager.getHighScores());
                    break;
                case SHOP:
                    if (inputHandler.isFirePressedAndConsume()) {
                        setCurrentState(GameState.MAIN_MENU);
                    }
                    gameWindow.getGameCanvas().renderShop(currentPlayer.getCredit());
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
                case WAVE_CLEARED:
                    SystemTimer.sleep(3000);
                    startNextWave();
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
        handleSpawning(delta);
        updateEntities(delta);

        if (logicRequiredThisLoop) {
            entityManager.doLogicAll();
            logicRequiredThisLoop = false;
        }
        gameWindow.getGameCanvas().render(entityManager.getEntities(), message, score, currentState, 0, wave, pauseMenu);
    }

    /**
     * 모든 종류의 엔티티(외계인, 운석, 폭탄) 생성을 처리합니다.
     */
    private void handleSpawning(long delta) {
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

        long currentTime = System.currentTimeMillis();

        // Spawn meteors
        if (currentTime - lastMeteorSpawnTime > meteorSpawnInterval) {
            lastMeteorSpawnTime = currentTime;
            int quantity = (int) (Math.random() * 2) + 2; // 2 or 3
            for (int i = 0; i < quantity; i++) {
                int x = (int) (Math.random() * 750);
                MeteorEntity meteor = new MeteorEntity(this, "sprites/meteor.gif", x, -50, 150);
                entityManager.addEntity(meteor);
            }
        }

        // Spawn bombs
        if (currentTime - lastBombSpawnTime > bombSpawnInterval) {
            lastBombSpawnTime = currentTime;
            int quantity = (int) (Math.random() * 2) + 2; // 2 or 3
            for (int i = 0; i < quantity; i++) {
                int x = (int) (Math.random() * 750);
                BombEntity bomb = new BombEntity(this, "sprites/bomb.gif", x, -50, 100, wave);
                entityManager.addEntity(bomb);
            }
        }
    }

    /**
     * 엔티티의 이동, 충돌, 제거 등 상태 업데이트를 처리합니다.
     */
    private void updateEntities(long delta) {
        entityManager.moveAll(delta);
        collisionDetector.checkCollisions(entityManager.getEntities());
        entityManager.cleanup();
    }

    private void handleMenuInput() {
        if (inputHandler.isLeftPressedAndConsume()) mainMenu.moveLeft();
        if (inputHandler.isRightPressedAndConsume()) mainMenu.moveRight();

        if (inputHandler.isFirePressedAndConsume()) {
            String selected = mainMenu.getSelectedItem();
            if ("1. 게임시작".equals(selected)) {
                startGameplay();
            } else if ("2. 랭킹".equals(selected)) {
                setCurrentState(GameState.RANKING);
            } else if ("4. 상점".equals(selected)) {
                setCurrentState(GameState.SHOP);
            } else if ("5. 설정".equals(selected)){
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
            else if ("메인메뉴로 나가기".equals(selected)) {
                saveGameResults(); // 일시정지 후 메뉴로 나갈때도 결과 저장
                setCurrentState(GameState.MAIN_MENU);
            }
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
        if (ship == null) return;
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

    private void saveGameResults() {
        if (user == null || currentPlayer == null) return;

        // 1. 새로운 크레딧과 최고 점수 계산
        int newCredit = currentPlayer.getCredit() + score;
        int newHighScore = Math.max(currentPlayer.getHighScore(), score);

        // 2. 변경된 내용을 PlayerData 객체에도 반영 (다음 게임을 위해)
        currentPlayer.setCredit(newCredit);
        currentPlayer.setHighScore(newHighScore);

        // 3. 데이터베이스에 업데이트 요청
        databaseManager.updatePlayerStats(user.getLocalId(), newCredit, newHighScore);
    }

    @Override
    public void notifyDeath() {
        saveGameResults();
        message = "Oh no! They got you, try again?";
        setCurrentState(GameState.GAME_OVER);
    }

    @Override
    public void notifyWin() {
        saveGameResults();
        message = "Well done! You Win!";
        setCurrentState(GameState.GAME_WON);
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

    @Override
    public java.util.List<Entity> getEntities() {
        return entityManager.getEntities();
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