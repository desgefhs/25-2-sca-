package org.newdawn.spaceinvaders;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.ShotEntity;

/**
 * 게임의 핵심 로직과 상태를 관리하며, 다른 전문 관리 클래스들을 총괄
 */
public class GameManager implements GameContext {

    private final InputHandler inputHandler;
    private final EntityManager entityManager;
    private final CollisionDetector collisionDetector;
    private final GameWindow gameWindow;

    private boolean gameRunning = true;
    private boolean logicRequiredThisLoop = false;
    private String message = "";
    private long lastFire = 0;
    private final long firingInterval = 500;
    private final double moveSpeed = 300;
    private static final int SHOT_DAMAGE = 1;

    public GameManager() {
        this.inputHandler = new InputHandler();
        // EntityManager에게 GameContext 구현체인 자신(this)을 넘겨준다.
        this.entityManager = new EntityManager(this);
        this.collisionDetector = new CollisionDetector();
        this.gameWindow = new GameWindow(inputHandler);
    }

    /**
     * 게임을 시작하고 메인 루프를 실행합니다.
     */
    public void startGame() {
        entityManager.initEntities();
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

            // 입력 처리
            if (inputHandler.checkStartGameKey()) {
                entityManager.initEntities();
                entityManager.getShip().getHealth().reset();
                message = "";
            }

            // 게임 로직 업데이트
            if (!inputHandler.isWaitingForKeyPress()) {
                handlePlayerInput(delta);
                entityManager.moveAll(delta);
                collisionDetector.checkCollisions(entityManager.getEntities());
                entityManager.cleanup();

                if (logicRequiredThisLoop) {
                    entityManager.doLogicAll();
                    logicRequiredThisLoop = false;
                }
            }

            // 화면 렌더링
            gameWindow.render(entityManager.getEntities(), message);

            SystemTimer.sleep(lastLoopTime + 10 - SystemTimer.getTime());
        }
    }

    private void handlePlayerInput(long delta) {
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
        } else if (inputHandler.isDownPressed() && !inputHandler.isUpPressed()) {
            ship.setVerticalMovement(moveSpeed);
        }

        if (inputHandler.isFirePressed()) {
            tryToFire();
        }
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();
        ShipEntity ship = entityManager.getShip();
        ShotEntity shot = new ShotEntity(this, "sprites/shot.gif", ship.getX() + 10, ship.getY() - 30, SHOT_DAMAGE, false);
        entityManager.addEntity(shot);
    }

    @Override
    public void notifyDeath() {
        message = "Oh no! They got you, try again?";
        inputHandler.setWaitingForKeyPress(true);
    }

    @Override
    public void notifyWin() {
        message = "Well done! You Win!";
        inputHandler.setWaitingForKeyPress(true);
    }

    @Override
    public void notifyAlienKilled() {
        entityManager.decreaseAlienCount();
        if (entityManager.getAlienCount() == 0) {
            notifyWin();
        }
        entityManager.speedUpAliens();
    }

    @Override
    public void updateLogic() {
        logicRequiredThisLoop = true;
    }

    @Override
    public void removeEntity(Entity entity) {
        entityManager.removeEntity(entity);
    }
}