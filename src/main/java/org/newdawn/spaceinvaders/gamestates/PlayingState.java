package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity;
import org.newdawn.spaceinvaders.entity.HealingAreaEntity;
import org.newdawn.spaceinvaders.core.CollisionDetector;
import org.newdawn.spaceinvaders.view.BuffUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;

public class PlayingState implements GameState {

    private final GameContext gameContext;
    private long lastMeteorSpawnTime;
    private long nextMeteorSpawnInterval;
    private final BuffUI buffUI;

    public PlayingState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.buffUI = new BuffUI();
    }

    @Override
    public void init() {}

    @Override
    public void handleInput(InputHandler input) {
        if (input.isHPressedAndConsume()) {
            gameContext.setShowHitboxes(!gameContext.getShowHitboxes());
        }
        if (input.isEscPressedAndConsume()) {
            gameContext.setCurrentState(Type.PAUSED);
            return;
        }

        handlePlayingInput(input);
    }

    @Override
    public void update(long delta) {
        gameContext.getBackground().update(delta);

        handleSpawning(delta);
        handleMeteorSpawning();
        handleHealingAreaSpawning();

        gameContext.getEntityManager().moveAll(delta);
        new CollisionDetector().checkCollisions(gameContext.getEntityManager().getEntities());
        gameContext.getEntityManager().cleanup();

        // Check for wave completion
        if (gameContext.getEntityManager().getAlienCount() == 0 && gameContext.getWaveManager().getFormationsSpawnedInWave() >= gameContext.getWaveManager().getFormationsPerWave()) {
            gameContext.onWaveCleared();
        }

        gameContext.setLogicRequiredThisLoop(true);
    }

    @Override
    public void render(Graphics2D g) {
        // Draw Background
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        gameContext.getBackground().draw(g);

        // --- Start of Clipped Drawing ---
        Shape originalClip = g.getClip();
        try {
            g.setClip(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

            // Draw Entities
            for (Entity entity : gameContext.getEntityManager().getEntities()) {
                entity.draw(g);
            }

            // Draw Hitboxes if enabled
            if (gameContext.getShowHitboxes()) {
                g.setColor(Color.RED);
                for (Entity entity : gameContext.getEntityManager().getEntities()) {
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
        g.drawString(String.format("점수: %03d", gameContext.getPlayerManager().getScore()), 680, 30);
        g.drawString(String.format("Wave: %d", gameContext.getWaveManager().getWave()), 520, 30);

        // Draw Play Time
        if (gameContext.getPlayerManager().getGameStartTime() > 0) {
            long elapsedMillis = System.currentTimeMillis() - gameContext.getPlayerManager().getGameStartTime();
            long elapsedSeconds = elapsedMillis / 1000;
            long minutes = elapsedSeconds / 60;
            long seconds = elapsedSeconds % 60;
            g.drawString(String.format("Time: %02d:%02d", minutes, seconds), 520, 55);
        }

        // Draw Buff UI
        if (gameContext.getShip() != null) {
            buffUI.draw(g, gameContext.getShip().getBuffManager());
        }

        // Draw Message if any
        if (gameContext.getMessage() != null && !gameContext.getMessage().isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(gameContext.getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getMessage())) / 2, 250);
        }
    }

    private void handlePlayingInput(InputHandler input) {
        ShipEntity ship = gameContext.getShip();
        if (ship == null) return;
        ship.setHorizontalMovement(0);
        ship.setVerticalMovement(0);

        if (input.isLeftPressed() && !input.isRightPressed()) ship.setHorizontalMovement(-ship.getMoveSpeed());
        else if (input.isRightPressed() && !input.isLeftPressed()) ship.setHorizontalMovement(ship.getMoveSpeed());

        if (input.isUpPressed() && !input.isDownPressed()) ship.setVerticalMovement(-gameContext.getMoveSpeed());
        if (input.isDownPressed() && !input.isUpPressed()) ship.setVerticalMovement(gameContext.getMoveSpeed());

        if (input.isFirePressed()) ship.tryToFire();

        if (input.isOnePressedAndConsume()) {
            org.newdawn.spaceinvaders.entity.weapon.Weapon weapon = gameContext.getWeapons().get("DefaultGun");
            weapon.setLevel(gameContext.getPlayerManager().getPlayerStats().getWeaponLevel("DefaultGun"));
            ship.setWeapon(weapon);
        }
        if (input.isTwoPressedAndConsume()) {
            if (gameContext.getPlayerManager().getPlayerStats().getWeaponLevel("Shotgun") > 0) {
                org.newdawn.spaceinvaders.entity.weapon.Weapon weapon = gameContext.getWeapons().get("Shotgun");
                weapon.setLevel(gameContext.getPlayerManager().getPlayerStats().getWeaponLevel("Shotgun"));
                ship.setWeapon(weapon);
            }
        }
        if (input.isThreePressedAndConsume()) {
            if (gameContext.getPlayerManager().getPlayerStats().getWeaponLevel("Laser") > 0) {
                org.newdawn.spaceinvaders.entity.weapon.Weapon weapon = gameContext.getWeapons().get("Laser");
                weapon.setLevel(gameContext.getPlayerManager().getPlayerStats().getWeaponLevel("Laser"));
                ship.setWeapon(weapon);
            }
        }

        if (input.isKPressedAndConsume()) {
            gameContext.getWaveManager().skipToNextBossWave();
        }
    }

    private void handleSpawning(long delta) {
        // Check if it's time to spawn the next formation in the wave
        if (gameContext.getWaveManager().getFormationsSpawnedInWave() < gameContext.getWaveManager().getFormationsPerWave() &&
            gameContext.getWaveManager().getNextFormationSpawnTime() > 0 && // Make sure timer is set
            System.currentTimeMillis() > gameContext.getWaveManager().getNextFormationSpawnTime()) {
            
            gameContext.getWaveManager().spawnNextFormationInWave();
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
            MeteorEntity meteor = new MeteorEntity(gameContext, randomType, xPos, -50);

            // 3. Set a random downward-diagonal velocity
            double speed = (Math.random() * 50) + 50; // Random base speed
            double angle = Math.toRadians(30 + Math.random() * 120); // Angle between 30 and 150 degrees (downward cone)
            meteor.setVerticalMovement(Math.sin(angle) * speed);
            meteor.setHorizontalMovement(Math.cos(angle) * speed);

            gameContext.addEntity(meteor);
        }
    }

    private void handleHealingAreaSpawning() {
        if (gameContext.getWaveManager().getWave() > 0 && gameContext.getWaveManager().getWave() % 8 == 0 && !gameContext.getWaveManager().isHealingAreaSpawnedForWave()) {
            int xPos = (int) (Math.random() * (Game.GAME_WIDTH - 50));
            gameContext.addEntity(new HealingAreaEntity(gameContext, xPos, -50));
            gameContext.getWaveManager().setHealingAreaSpawnedForWave(true);
        }
    }
}