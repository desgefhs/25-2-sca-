package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.core.CollisionDetector;
import org.newdawn.spaceinvaders.view.PlayingStateRenderer;

import java.awt.Graphics2D;

public class PlayingState implements GameState {

    private final GameContext gameContext;
    private final PlayingStateRenderer renderer;

    public PlayingState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.renderer = new PlayingStateRenderer(gameContext);
    }

    @Override
    public void init() {
        // The init logic is now part of onEnter to ensure it's called every time we enter the state
    }

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

        // All spawning logic is now delegated to WaveManager
        gameContext.getWaveManager().update(delta);

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
        renderer.render(g);
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

    @Override
    public void onEnter() {
        // Initialize the wave manager's timers every time we enter the playing state
        gameContext.getWaveManager().init();
    }

    @Override
    public void onExit() {}
}