package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.*;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity;

import org.newdawn.spaceinvaders.view.BuffUI;

import java.awt.*;

/**
 * 실제 게임 플레이가 이루어지는 상태를 처리하는 클래스
 * 플레이어 입력, 엔티티 업데이트, 렌더링 등 게임의 핵심 로직을 담당
 */
public class PlayingState implements GameState {

    private final GameManager gameManager;
    /** 마지막 운석 생성 시간 */
    private long lastMeteorSpawnTime;
    /** 다음 운석 생성까지의 간격 */
    private long nextMeteorSpawnInterval;
    /** 버프 UI */
    private final BuffUI buffUI;

    public PlayingState(GameManager gameManager) {
        this.gameManager = gameManager;
        this.buffUI = new BuffUI();
    }

    @Override
    public void init() {}

    /**
     * 플레이 중 상태의 사용자 입력을 처리
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        // H 키로 히트박스 표시 토글
        if (input.isHPressedAndConsume()) {
            gameManager.showHitboxes = !gameManager.showHitboxes;
        }
        // ESC 키로 일시정지 메뉴
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.PAUSED);
            return;
        }

        handlePlayingInput(input);
    }

    /**
     * 게임 월드를 업데이트
     *
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    @Override
    public void update(long delta) {
        gameManager.background.update(delta);

        // 각종 스폰 로직 처리
        handleSpawning(delta);
        handleMeteorSpawning();
        handleHealingAreaSpawning();

        // 엔티티 이동, 충돌 감지, 정리
        gameManager.getEntityManager().moveAll(delta);
        new CollisionDetector().checkCollisions(gameManager.getEntityManager().getEntities());
        gameManager.getEntityManager().cleanup();

        // 웨이브 클리어 조건 확인
        if (gameManager.getEntityManager().getAlienCount() == 0 && gameManager.formationsSpawnedInWave >= gameManager.formationsPerWave) {
            gameManager.setCurrentState(GameState.Type.WAVE_CLEARED);
        }

        // 프레임당 한 번만 실행되어야 하는 로직 처리
        if (gameManager.logicRequiredThisLoop) {
            gameManager.getEntityManager().doLogicAll();
            gameManager.logicRequiredThisLoop = false;
        }
    }

    /**
     * 게임 화면을 렌더링
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        // 배경 그리기
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        gameManager.background.draw(g);

        // 게임 영역(Clipped) 렌더링
        renderGameArea(g);

        // UI 렌더링
        renderUI(g);
    }

    /**
     * 게임 플레이와 관련된 사용자 입력을 처리 (이동, 발사, 무기 교체 등)
     *
     * @param input 입력 핸들러
     */
    private void handlePlayingInput(InputHandler input) {
        ShipEntity ship = gameManager.getShip();
        if (ship == null) return;
        ship.setHorizontalMovement(0);
        ship.setVerticalMovement(0);

        // 좌우 이동
        if (input.isLeftPressed() && !input.isRightPressed()) ship.setHorizontalMovement(-ship.getMoveSpeed());
        else if (input.isRightPressed() && !input.isLeftPressed()) ship.setHorizontalMovement(ship.getMoveSpeed());

        // 상하 이동
        if (input.isUpPressed() && !input.isDownPressed()) ship.setVerticalMovement(-gameManager.moveSpeed);
        if (input.isDownPressed() && !input.isUpPressed()) ship.setVerticalMovement(gameManager.moveSpeed);

        // 발사
        if (input.isFirePressed()) ship.tryToFire();

        // 무기 교체 (1, 2, 3)
        if (input.isOnePressedAndConsume()) {
            org.newdawn.spaceinvaders.entity.weapon.Weapon weapon = gameManager.getWeapons().get("DefaultGun");
            weapon.setLevel(gameManager.playerStats.getWeaponLevel("DefaultGun"));
            ship.setWeapon(weapon);
        }
        if (input.isTwoPressedAndConsume()) {
            if (gameManager.playerStats.getWeaponLevel("Shotgun") > 0) {
                org.newdawn.spaceinvaders.entity.weapon.Weapon weapon = gameManager.getWeapons().get("Shotgun");
                weapon.setLevel(gameManager.playerStats.getWeaponLevel("Shotgun"));
                ship.setWeapon(weapon);
            }
        }
        if (input.isThreePressedAndConsume()) {
            if (gameManager.playerStats.getWeaponLevel("Laser") > 0) {
                org.newdawn.spaceinvaders.entity.weapon.Weapon weapon = gameManager.getWeapons().get("Laser");
                weapon.setLevel(gameManager.playerStats.getWeaponLevel("Laser"));
                ship.setWeapon(weapon);
            }
        }

        // K 키로 다음 보스 웨이브로 스킵 (디버그용)
        if (input.isKPressedAndConsume()) {
            int targetWave = ((gameManager.wave / 5) * 5) + 5;
            gameManager.setWave(targetWave);
            gameManager.startNextWave();
        }
    }

    /**
     * 적 포메이션 생성을 처리
     */
    private void handleSpawning(long delta) {

        if (gameManager.formationsSpawnedInWave < gameManager.formationsPerWave &&
            gameManager.nextFormationSpawnTime > 0 &&
            System.currentTimeMillis() > gameManager.nextFormationSpawnTime) {
            
            gameManager.spawnNextFormationInWave();
        }
    }

    /**
     * 상태 진입 시 운석 생성 타이머를 초기화
     */
    @Override
    public void onEnter() {
        lastMeteorSpawnTime = System.currentTimeMillis();
        nextMeteorSpawnInterval = 1000 + (long) (Math.random() * 1000);
    }

    @Override
    public void onExit() {}

    /**
     * 운석 생성을 처리
     */
    private void handleMeteorSpawning() {
        long currentTime = System.currentTimeMillis();
        if (currentTime > lastMeteorSpawnTime + nextMeteorSpawnInterval) {
            lastMeteorSpawnTime = currentTime;
            nextMeteorSpawnInterval = 1000 + (long) (Math.random() * 1000); // 1~2초 간격으로 재생성

            MeteorEntity.MeteorType randomType = MeteorEntity.MeteorType.values()[(int) (Math.random() * MeteorEntity.MeteorType.values().length)];
            int xPos = (int) (Math.random() * (Game.GAME_WIDTH - 50));
            MeteorEntity meteor = new MeteorEntity(gameManager, randomType, xPos, -50);

            double speed = (Math.random() * 50) + 50;
            double angle = Math.toRadians(30 + Math.random() * 120); // 30~150도 사이의 하강 각도
            meteor.setVerticalMovement(Math.sin(angle) * speed);
            meteor.setHorizontalMovement(Math.cos(angle) * speed);

            gameManager.addEntity(meteor);
        }
    }

    /**
     * 힐링 영역 생성을 처리 (8 웨이브마다)
     */
    private void handleHealingAreaSpawning() {
        if (gameManager.wave > 0 && gameManager.wave % 8 == 0 && !gameManager.healingAreaSpawnedForWave) {
            int xPos = (int) (Math.random() * (Game.GAME_WIDTH - 50));
            gameManager.addEntity(new HealingAreaEntity(gameManager, xPos, -50));
            gameManager.healingAreaSpawnedForWave = true;
        }
    }

    /**
     * 게임 영역 내의 엔티티들을 렌더링
     */
    private void renderGameArea(Graphics2D g) {
        Shape originalClip = g.getClip();
        try {
            g.setClip(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

            // 엔티티 그리기
            for (Entity entity : gameManager.getEntityManager().getEntities()) {
                entity.draw(g);
            }

            // 히트박스 그리기 (활성화 시)
            if (gameManager.showHitboxes) {
                g.setColor(Color.RED);
                for (Entity entity : gameManager.getEntityManager().getEntities()) {
                    g.drawRect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
                }
            }
        } finally {
            g.setClip(originalClip); // 클립 영역 복원
        }
    }

    /**
     * 점수, 웨이브, 시간, 버프 등 UI를 렌더링
     */
    private void renderUI(Graphics2D g) {
        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 14));
        g.drawString(String.format("점수: %03d", gameManager.score), 680, 30);
        g.drawString(String.format("Wave: %d", gameManager.wave), 520, 30);

        // 플레이 시간
        if (gameManager.getGameStartTime() > 0) {
            long elapsedSeconds = (System.currentTimeMillis() - gameManager.getGameStartTime()) / 1000;
            g.drawString(String.format("Time: %02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60), 520, 55);
        }

        // 버프 UI
        if (gameManager.getShip() != null) {
            buffUI.draw(g, gameManager.getShip().getBuffManager());
        }

        // 중앙 메시지
        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(gameManager.message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.message)) / 2, 250);
        }
    }
}