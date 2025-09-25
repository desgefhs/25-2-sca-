package org.newdawn.spaceinvaders;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.ShotEntity;
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
    private double backgroundY = 0;
    private final double BACKGROUND_SCROLL_SPEED = 50;

    public GameManager() {
        this.inputHandler = new InputHandler();
        this.entityManager = new EntityManager(this);
        this.collisionDetector = new CollisionDetector();
        this.gameWindow = new GameWindow(inputHandler);
        this.mainMenu = new MainMenu();
        this.pauseMenu = new PauseMenu();
        this.currentState = GameState.MAIN_MENU; // 초기 상태를 메인 메뉴로 설정
    }

    /**
     * 게임을 시작하고 메인 루프를 실행합니다.
     */
    public void startGame() {
        mainLoop();
    }

    private void mainLoop() {
        long lastLoopTime = SystemTimer.getTime();
        long lastFpsTime = 0;
        int fps = 0;

        while (gameRunning) {
            long delta = SystemTimer.getTime() - lastLoopTime;
            lastLoopTime = SystemTimer.getTime();

            // FPS 계산
            lastFpsTime += delta;
            fps++;
            if (lastFpsTime >= 1000) {
                gameWindow.setTitle("Space Invaders (FPS: " + fps + ")");
                lastFpsTime = 0;
                fps = 0;
            }

            // 현재 게임 상태에 따라 로직 분기
            switch (currentState) {
                case MAIN_MENU:
                    handleMenuInput();
                    gameWindow.renderMenu(mainMenu);
                    break;
                case PLAYING:
                    if (inputHandler.isPPressedAndConsume()) {
                        currentState = GameState.PAUSED;
                        break;
                    }
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
                    handlePlayingInput(delta);
                    entityManager.moveAll(delta);
                    collisionDetector.checkCollisions(entityManager.getEntities());
                    entityManager.cleanup();
                    if (logicRequiredThisLoop) {
                        entityManager.doLogicAll();
                        logicRequiredThisLoop = false;
                    }
                    gameWindow.render(entityManager.getEntities(), message, score, currentState, backgroundY, wave, pauseMenu);
                    break;
                case WAVE_CLEARED:
                    startNextWave();
                    currentState = GameState.PLAYING;
                    break;
                case PAUSED:
                    handlePauseMenuInput();
                    gameWindow.render(entityManager.getEntities(), message, score, currentState, backgroundY, wave, pauseMenu);
                    break;
                case GAME_OVER:
                case GAME_WON: // GAME_OVER와 GAME_WON 상태는 동일한 로직을 공유
                    if (inputHandler.isFirePressedAndConsume()) {
                        currentState = GameState.MAIN_MENU;
                    }
                    gameWindow.render(entityManager.getEntities(), message, score, currentState, backgroundY, wave, pauseMenu);
                    break;
            }

            SystemTimer.sleep(lastLoopTime + 10 - SystemTimer.getTime());
        }
    }

    private void handlePauseMenuInput() {
        if (inputHandler.isUpPressedAndConsume()) {
            pauseMenu.moveUp();
        }
        if (inputHandler.isDownPressedAndConsume()) {
            pauseMenu.moveDown();
        }
        if (inputHandler.isFirePressedAndConsume()) {
            String selected = pauseMenu.getSelectedItem();
            switch (selected) {
                case "재개하기":
                    currentState = GameState.PLAYING;
                    break;
                case "메인메뉴로 나가기":
                    currentState = GameState.MAIN_MENU;
                    break;
                case "종료하기":
                    System.exit(0);
                    break;
            }
        }
    }

    private void handleMenuInput() {
        if (inputHandler.isLeftPressedAndConsume()) {
            mainMenu.moveLeft();
        }
        if (inputHandler.isRightPressedAndConsume()) {
            mainMenu.moveRight();
        }

        if (inputHandler.isFirePressedAndConsume()) {
            String selected = mainMenu.getSelectedItem();
            if ("1. 게임시작".equals(selected)) {
                startGameplay();
            } else if ("4. 설정".equals(selected)){
                System.exit(0);
            }
        }
    }

    private void startGameplay() {
        resetScore();
        wave = 1;
        lineCount = 0;
        lastLineSpawnTime = System.currentTimeMillis();
        entityManager.initShip();
        currentState = GameState.PLAYING;
        message = "";
    }

    private void handlePlayingInput(long delta) {
        ShipEntity ship = entityManager.getShip();
        ship.setHorizontalMovement(0);
        ship.setVerticalMovement(0);

        if (inputHandler.isLeftPressed() && !inputHandler.isRightPressed()) {
            ship.setHorizontalMovement(-moveSpeed);
        } else if (inputHandler.isRightPressed() && !inputHandler.isLeftPressed()) {
            ship.setHorizontalMovement(moveSpeed);
        }

        if (inputHandler.isUpPressed() && !inputHandler.isDownPressed()) {
            ship.setVerticalMovement(-moveSpeed);
        }
        if (inputHandler.isDownPressed() && !inputHandler.isUpPressed()) {
            ship.setVerticalMovement(moveSpeed);
        }

        if (inputHandler.isFirePressed()) {
            tryToFire();
        }
    }

    @Override
    public void addEntity(Entity entity) {
        entityManager.addEntity(entity);
    }

    private void tryToFire() {
        // 발사 간격(연사 속도) 체크
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();
        ShipEntity ship = entityManager.getShip();

        // 기본: 중앙에서 한 발 발사
        ShotEntity shot = new ShotEntity(this, "sprites/shot.gif", ship.getX() + 10, ship.getY() - 30, SHOT_DAMAGE);
        entityManager.addEntity(shot);
    }

    @Override
    public void notifyDeath() {
        message = "Oh no! They got you, try again?";
        currentState = GameState.GAME_OVER;
    }

    public void startNextWave() {
        wave++;
        lineCount = 0;
        lastLineSpawnTime = System.currentTimeMillis();
        message = "Wave " + wave;
        entityManager.initShip();
    }

    @Override
    public void notifyWin() {
        message = "Well done! You Win!";
        currentState = GameState.GAME_WON;
    }

    @Override
    public void notifyAlienEscaped(Entity entity) {
        entityManager.removeEntity(entity);
        entityManager.decreaseAlienCount();
        if (entityManager.getAlienCount() == 0 && lineCount >= LINES_PER_WAVE) {
            currentState = GameState.WAVE_CLEARED;
        }
    }

    @Override
    public void notifyAlienKilled() {
        increaseScore(ALIEN_SCORE);
        entityManager.decreaseAlienCount();
        if (entityManager.getAlienCount() == 0 && lineCount >= LINES_PER_WAVE) {
            currentState = GameState.WAVE_CLEARED;
        }
    }

    @Override
    public void updateLogic() {
        logicRequiredThisLoop = true;
    }

    @Override
    public void removeEntity(Entity entity) {
        entityManager.removeEntity(entity);
    }

    public void increaseScore(int amount) {
        score += amount;
    }

    public void resetScore() {
        score = 0;
    }

    public int getScore() {
        return score;
    }
}
