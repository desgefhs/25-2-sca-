package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.*;
import org.newdawn.spaceinvaders.entity.*;

import java.awt.*;

public class PlayingState implements GameState {

    private final GameManager gameManager;

    public PlayingState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {}

    @Override
    public void handleInput(InputHandler input) {
        if (input.isHPressedAndConsume()) {
            gameManager.showHitboxes = !gameManager.showHitboxes;
        }
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.PAUSED);
            return;
        }

        handlePlayingInput(input);
    }

    @Override
    public void update(long delta) {
        gameManager.background.update(delta);

        handleSpawning(delta);

        gameManager.getEntityManager().moveAll(delta);
        new CollisionDetector().checkCollisions(gameManager.getEntityManager().getEntities());
        gameManager.getEntityManager().cleanup();

        if (gameManager.logicRequiredThisLoop) {
            gameManager.getEntityManager().doLogicAll();
            gameManager.logicRequiredThisLoop = false;
        }
    }

    @Override
    public void render(Graphics2D g) {

        //배경 그리기
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        gameManager.background.draw(g);

        // 엔티티 그리기
        for (Entity entity : gameManager.getEntityManager().getEntities()) {
            entity.draw(g);
        }

        // 히트박스 확인용 (H키)
        if (gameManager.showHitboxes) {
            g.setColor(Color.RED);
            for (Entity entity : gameManager.getEntityManager().getEntities()) {
                g.drawRect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
            }
        }

        // UI(스코어,웨이브 상태)
        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 14));
        g.drawString(String.format("점수: %03d", gameManager.score), 680, 30);
        g.drawString(String.format("Wave: %d", gameManager.wave), 20, 30);

        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(gameManager.message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.message)) / 2, 250);
        }
    }

    private void handlePlayingInput(InputHandler input) {
        ShipEntity ship = gameManager.getShip();
        if (ship == null) return;
        ship.setHorizontalMovement(0);
        ship.setVerticalMovement(0);

        if (input.isLeftPressed() && !input.isRightPressed()) ship.setHorizontalMovement(-gameManager.moveSpeed);
        else if (input.isRightPressed() && !input.isLeftPressed()) ship.setHorizontalMovement(gameManager.moveSpeed);

        if (input.isUpPressed() && !input.isDownPressed()) ship.setVerticalMovement(-gameManager.moveSpeed);
        if (input.isDownPressed() && !input.isUpPressed()) ship.setVerticalMovement(gameManager.moveSpeed);

        if (input.isFirePressed()) ship.tryToFire();

        if (input.isOnePressedAndConsume()) {
            org.newdawn.spaceinvaders.entity.weapon.Weapon weapon = gameManager.getWeapons().get("DefaultGun");
            weapon.setLevel(gameManager.playerStats.getWeaponLevel("DefaultGun"));
            ship.setWeapon(weapon);
        }
        if (input.isTwoPressedAndConsume()) {
            if (gameManager.playerStats.getWeaponLevel("Flamethrower") > 0) {
                org.newdawn.spaceinvaders.entity.weapon.Weapon weapon = gameManager.getWeapons().get("Flamethrower");
                weapon.setLevel(gameManager.playerStats.getWeaponLevel("Flamethrower"));
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

        if (input.isKPressedAndConsume()) {
            int targetWave = ((gameManager.wave / 5) * 5) + 5;
            gameManager.setWave(targetWave);
            gameManager.startNextWave();
        }
    }

    //적 생성
    private void handleSpawning(long delta) {
        int effectiveWave = ((gameManager.wave - 1) % 5) + 1;
        if (effectiveWave == 5) { //보스 스테이지
            if (gameManager.lineCount == 0) {
                gameManager.getEntityManager().spawnNext(gameManager.wave, gameManager.lineCount);
                gameManager.lineCount++;
            }
        } else { // 일반 스테이지
            if (gameManager.lineCount < gameManager.LINES_PER_WAVE) {
                if (System.currentTimeMillis() - gameManager.lastLineSpawnTime > gameManager.lineSpawnInterval) {
                    gameManager.getEntityManager().spawnNext(gameManager.wave, gameManager.lineCount);
                    gameManager.lineCount++;
                    gameManager.lastLineSpawnTime = System.currentTimeMillis();
                }
            }
        }

        long currentTime = System.currentTimeMillis();

        //메테오 생성(임시)
        if (currentTime - gameManager.lastMeteorSpawnTime > gameManager.meteorSpawnInterval) {
            gameManager.lastMeteorSpawnTime = currentTime;
            int quantity = (int) (Math.random() * 2) + 2;
            for (int i = 0; i < quantity; i++) {
                MeteorEntity meteor = new MeteorEntity(gameManager, "sprites/meteor.gif", 0, -50, 150);
                int x = (int) (Math.random() * (Game.GAME_WIDTH - meteor.getWidth()));
                meteor.setX(x);
                gameManager.addEntity(meteor);
            }
        }

        //폭탄 엔티티 생성(임시)
        if (currentTime - gameManager.lastBombSpawnTime > gameManager.bombSpawnInterval) {
            gameManager.lastBombSpawnTime = currentTime;
            int quantity = (int) (Math.random() * 2) + 2;
            for (int i = 0; i < quantity; i++) {
                BombEntity bomb = new BombEntity(gameManager, "sprites/enemy/bomb.gif", 0, -50, 100, gameManager.wave);
                int x = (int) (Math.random() * (Game.GAME_WIDTH - bomb.getWidth()));
                bomb.setX(x);
                gameManager.addEntity(bomb);
            }
        }
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {}
}
