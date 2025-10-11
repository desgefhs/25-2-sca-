package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.*;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity;

import org.newdawn.spaceinvaders.view.BuffUI;

import java.awt.*;

public class PlayingState implements GameState {

    private final GameManager gameManager;
    private long lastMeteorSpawnTime;
    private long nextMeteorSpawnInterval;
    private final BuffUI buffUI;

    public PlayingState(GameManager gameManager) {
        this.gameManager = gameManager;
        this.buffUI = new BuffUI();
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
        handleMeteorSpawning();
        handleHealingAreaSpawning();

        gameManager.getEntityManager().moveAll(delta);
        new CollisionDetector().checkCollisions(gameManager.getEntityManager().getEntities());
        gameManager.getEntityManager().cleanup();

        // Check for wave completion
        if (gameManager.getEntityManager().getAlienCount() == 0 && gameManager.formationsSpawnedInWave >= gameManager.formationsPerWave) {
            gameManager.setCurrentState(GameState.Type.WAVE_CLEARED);
        }

        if (gameManager.logicRequiredThisLoop) {
            gameManager.getEntityManager().doLogicAll();
            gameManager.logicRequiredThisLoop = false;
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Draw Background
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        gameManager.background.draw(g);

        // --- Start of Clipped Drawing ---
        Shape originalClip = g.getClip();
        try {
            g.setClip(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

            // Draw Entities
            for (Entity entity : gameManager.getEntityManager().getEntities()) {
                entity.draw(g);
            }

            // Draw Hitboxes if enabled
            if (gameManager.showHitboxes) {
                g.setColor(Color.RED);
                for (Entity entity : gameManager.getEntityManager().getEntities()) {
                    g.drawRect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
                }
            }
        } finally {
            // Restore original clip to draw UI elements outside the game area
            g.setClip(originalClip);
        }
        // --- End of Clipped Drawing ---

        // Draw UI
        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 14));
        g.drawString(String.format("점수: %03d", gameManager.score), 680, 30);
        g.drawString(String.format("Wave: %d", gameManager.wave), 520, 30);

        // Draw Play Time
        if (gameManager.getGameStartTime() > 0) {
            long elapsedMillis = System.currentTimeMillis() - gameManager.getGameStartTime();
            long elapsedSeconds = elapsedMillis / 1000;
            long minutes = elapsedSeconds / 60;
            long seconds = elapsedSeconds % 60;
            g.drawString(String.format("Time: %02d:%02d", minutes, seconds), 520, 55);
        }

        // Draw Buff UI
        if (gameManager.getShip() != null) {
            buffUI.draw(g, gameManager.getShip().getBuffManager());
        }

        // Draw Message if any
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

        if (input.isLeftPressed() && !input.isRightPressed()) ship.setHorizontalMovement(-ship.getMoveSpeed());
        else if (input.isRightPressed() && !input.isLeftPressed()) ship.setHorizontalMovement(ship.getMoveSpeed());

        if (input.isUpPressed() && !input.isDownPressed()) ship.setVerticalMovement(-gameManager.moveSpeed);
        if (input.isDownPressed() && !input.isUpPressed()) ship.setVerticalMovement(gameManager.moveSpeed);

        if (input.isFirePressed()) ship.tryToFire();

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

        if (input.isKPressedAndConsume()) {
            int targetWave = ((gameManager.wave / 5) * 5) + 5;
            gameManager.setWave(targetWave);
            gameManager.startNextWave();
        }
    }

    private void handleSpawning(long delta) {
        // Check if it's time to spawn the next formation in the wave
        if (gameManager.formationsSpawnedInWave < gameManager.formationsPerWave &&
            gameManager.nextFormationSpawnTime > 0 && // Make sure timer is set
            System.currentTimeMillis() > gameManager.nextFormationSpawnTime) {
            
            gameManager.spawnNextFormationInWave();
        }
    }

    @Override
    public void onEnter() {
        lastMeteorSpawnTime = System.currentTimeMillis();
        // Set initial random interval between 1-2 seconds
        nextMeteorSpawnInterval = 1000 + (long) (Math.random() * 1000);
    }

    @Override
    public void onExit() {}

    private void handleMeteorSpawning() {
        long currentTime = System.currentTimeMillis();
        if (currentTime > lastMeteorSpawnTime + nextMeteorSpawnInterval) {
            lastMeteorSpawnTime = currentTime;
            nextMeteorSpawnInterval = 1000 + (long) (Math.random() * 1000); // Reset for next spawn

            // 1. Select a random meteor type
            MeteorEntity.MeteorType[] types = MeteorEntity.MeteorType.values();
            MeteorEntity.MeteorType randomType = types[(int) (Math.random() * types.length)];

            // 2. Create the meteor at a random X position at the top of the screen
            int xPos = (int) (Math.random() * (Game.GAME_WIDTH - 50)); // -50 to avoid spawning partially off-screen
            MeteorEntity meteor = new MeteorEntity(gameManager, randomType, xPos, -50);

            // 3. Set a random downward-diagonal velocity
            double speed = (Math.random() * 50) + 50; // Random base speed
            double angle = Math.toRadians(30 + Math.random() * 120); // Angle between 30 and 150 degrees (downward cone)
            meteor.setVerticalMovement(Math.sin(angle) * speed);
            meteor.setHorizontalMovement(Math.cos(angle) * speed);

            gameManager.addEntity(meteor);
        }
    }

    private void handleHealingAreaSpawning() {
        if (gameManager.wave > 0 && gameManager.wave % 8 == 0 && !gameManager.healingAreaSpawnedForWave) {
            int xPos = (int) (Math.random() * (Game.GAME_WIDTH - 50));
            gameManager.addEntity(new HealingAreaEntity(gameManager, xPos, -50));
            gameManager.healingAreaSpawnedForWave = true;
        }
    }
}