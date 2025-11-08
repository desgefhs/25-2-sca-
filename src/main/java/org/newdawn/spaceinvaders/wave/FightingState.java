package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity;
import org.newdawn.spaceinvaders.entity.HealingAreaEntity;

public class FightingState implements WaveState {

    @Override
    public void onEnter(WaveManager waveManager) {
        // The regular fighting phase begins.
    }

    @Override
    public void update(WaveManager waveManager, long delta) {
        // This state now handles this logic directly.
        handleMeteorSpawning(waveManager);
        handleHealingAreaSpawning(waveManager);

        // Check if all aliens are defeated to end the wave.
        if (waveManager.getGameManager().getEntityManager().getAlienCount() <= 0) {
            // Instead of changing to a new state, directly trigger the next wave logic.
            waveManager.getGameManager().onWaveCleared();
        }
    }

    @Override
    public void onExit(WaveManager waveManager) {
        // Called when a new wave starts.
    }

    // Logic moved from WaveManager
    private void handleMeteorSpawning(WaveManager waveManager) {
        long currentTime = System.currentTimeMillis();
        if (currentTime > waveManager.getLastMeteorSpawnTime() + waveManager.getNextMeteorSpawnInterval()) {
            waveManager.setLastMeteorSpawnTime(currentTime);
            waveManager.setNextMeteorSpawnInterval(1000 + (long) (Math.random() * 1000));

            MeteorEntity.MeteorType[] types = MeteorEntity.MeteorType.values();
            MeteorEntity.MeteorType randomType = types[(int) (Math.random() * types.length)];

            int xPos = (int) (Math.random() * (Game.GAME_WIDTH - 50));
            MeteorEntity meteor = new MeteorEntity(waveManager.getGameManager(), randomType, xPos, -50);

            double speed = (Math.random() * 50) + 50;
            double angle = Math.toRadians(30 + Math.random() * 120);
            meteor.setVerticalMovement(Math.sin(angle) * speed);
            meteor.setHorizontalMovement(Math.cos(angle) * speed);

            waveManager.getGameManager().addEntity(meteor);
        }
    }

    // Logic moved from WaveManager
    private void handleHealingAreaSpawning(WaveManager waveManager) {
        if (waveManager.getWave() > 0 && waveManager.getWave() % 8 == 0 && !waveManager.isHealingAreaSpawnedForWave()) {
            int xPos = (int) (Math.random() * (Game.GAME_WIDTH - 50));
            waveManager.getGameManager().addEntity(new HealingAreaEntity(waveManager.getGameManager(), xPos, -50));
            waveManager.setHealingAreaSpawnedForWave(true);
        }
    }
}
