package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.BossFactory;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.events.GameWonEvent;

/**
 * Manages the progression of enemy waves in the game.
 * This class has been refactored to act as a "Wave Executor". It uses a WaveLoader
 * to get the definition for a wave and then executes the spawn events described
 * in the WaveDefinition. This separates the data (what a wave is) from the logic

 * (how to spawn the entities).
 */
public class WaveManager {

    private final GameManager gameManager;
    private final FormationManager formationManager;
    private final WaveLoader waveLoader; // New dependency

    private int wave = 0;
    private boolean healingAreaSpawnedForWave = false;

    // New state fields for wave execution
    private WaveDefinition currentWaveDefinition;
    private int currentSpawnIndex;
    private long nextSpawnTime;

    // Meteor spawning fields
    private long lastMeteorSpawnTime;
    private long nextMeteorSpawnInterval;

    public WaveManager(GameManager gameManager, FormationManager formationManager) {
        this.gameManager = gameManager;
        this.formationManager = formationManager;
        this.waveLoader = new WaveLoader(); // Instantiate the loader directly
    }

    /**
     * Initializes timers. Called when entering a playing state.
     */
    public void init() {
        lastMeteorSpawnTime = System.currentTimeMillis();
        nextMeteorSpawnInterval = 1000 + (long) (Math.random() * 1000);
    }

    /**
     * The main update loop for the wave manager.
     * Checks if it's time to spawn the next event in the current wave definition.
     * @param delta Time since the last frame.
     */
    public void update(long delta) {
        if (currentWaveDefinition == null || currentSpawnIndex >= currentWaveDefinition.getSpawns().size()) {
            // Wave is not active or all spawn events are complete.
            return;
        }

        if (System.currentTimeMillis() >= nextSpawnTime) {
            executeCurrentSpawn();
        }
    }

    /**
     * Executes the spawn event at the current index in the wave definition's spawn list.
     */
    private void executeCurrentSpawn() {
        SpawnInfo spawn = currentWaveDefinition.getSpawns().get(currentSpawnIndex);

        // Execute the spawn based on its type
        if ("FORMATION".equals(spawn.getType())) {
            spawnFormation(spawn);
        } else if ("BOSS".equals(spawn.getType())) {
            spawnBoss(spawn);
        }

        currentSpawnIndex++;

        // Schedule the next spawn event
        if (currentSpawnIndex < currentWaveDefinition.getSpawns().size()) {
            SpawnInfo nextSpawn = currentWaveDefinition.getSpawns().get(currentSpawnIndex);
            nextSpawnTime = System.currentTimeMillis() + nextSpawn.getDelay();
        } else {
            // All spawns for this wave are done. Mark definition as null to stop updates.
            currentWaveDefinition = null;
        }
    }

    /**
     * Spawns a formation based on the given SpawnInfo.
     * @param spawn The spawn information for the formation.
     */
    private void spawnFormation(SpawnInfo spawn) {
        Formation formation = formationManager.getRandomFormationForStage(spawn.getStage());
        gameManager.getGameContainer().getEntityManager().spawnFormation(formation, wave, spawn.isForceUpgrade());
    }

    /**
     * Spawns a boss based on the given SpawnInfo.
     * @param spawn The spawn information for the boss.
     */
    private void spawnBoss(SpawnInfo spawn) {
        // Clear all entities except the player ship before a boss fight.
        gameManager.getGameContainer().getEntityManager().getEntities().removeIf(entity -> !(entity instanceof ShipEntity));

        int waveNumberForBoss = spawn.getStage(); // Re-using stage field for boss wave number
        int cycle = (waveNumberForBoss - 1) / 5;
        double cycleMultiplier = Math.pow(1.5, cycle);
        int bossHealth = (int) (50 * cycleMultiplier);
        Entity boss = BossFactory.createBoss(gameManager, waveNumberForBoss, Game.GAME_WIDTH / 2, 50, bossHealth);
        gameManager.addEntity(boss);
        gameManager.getGameContainer().getEntityManager().setAlienCount(1);
    }

    public void startFirstWave() {
        wave = 0;
        startNextWave();
    }

    public void skipToNextBossWave() {
        this.wave = ((this.wave / 5) * 5) + 4; // Set to the wave before the next boss
        startNextWave();
    }

    /**
     * Starts the next wave by loading its definition and scheduling the first spawn.
     */
    public void startNextWave() {
        wave++;
        healingAreaSpawnedForWave = false;

        this.currentWaveDefinition = waveLoader.loadWave(wave);

        if (currentWaveDefinition == null) {
            if (wave > 25) { // Win condition
                gameManager.getEventBus().publish(new GameWonEvent());
            }
            return;
        }

        gameManager.setMessage("Wave " + wave);
        gameManager.setMessageEndTime(System.currentTimeMillis() + 1000);

        // Set music if defined for the wave
        if (currentWaveDefinition.getMusic() != null) {
            if (currentWaveDefinition.getMusic().equals("boss1")) {
                gameManager.getSoundManager().stopSound("gamebackground");
                gameManager.getSoundManager().loopSound("boss1");
            } else {
                gameManager.getSoundManager().stopSound("boss1");
                gameManager.getSoundManager().loopSound("gamebackground");
            }
        }

        // Reset spawn state and schedule the first spawn
        this.currentSpawnIndex = 0;
        if (!currentWaveDefinition.getSpawns().isEmpty()) {
            this.nextSpawnTime = System.currentTimeMillis() + currentWaveDefinition.getSpawns().get(0).getDelay();
        }

        gameManager.setCurrentState(GameState.Type.PLAYING);
    }

    public boolean hasFinishedSpawning() {
        // If there is no active wave definition, spawning is considered finished.
        if (currentWaveDefinition == null) {
            return true;
        }
        // Otherwise, check if all spawn events have been executed.
        return currentSpawnIndex >= currentWaveDefinition.getSpawns().size();
    }

    // --- Getters and Setters ---

    public int getWave() {
        return wave;
    }

    public boolean isHealingAreaSpawnedForWave() {
        return healingAreaSpawnedForWave;
    }

    public void setHealingAreaSpawnedForWave(boolean healingAreaSpawnedForWave) {
        this.healingAreaSpawnedForWave = healingAreaSpawnedForWave;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public long getLastMeteorSpawnTime() {
        return lastMeteorSpawnTime;
    }

    public void setLastMeteorSpawnTime(long lastMeteorSpawnTime) {
        this.lastMeteorSpawnTime = lastMeteorSpawnTime;
    }

    public long getNextMeteorSpawnInterval() {
        return nextMeteorSpawnInterval;
    }

    public void setNextMeteorSpawnInterval(long nextMeteorSpawnInterval) {
        this.nextMeteorSpawnInterval = nextMeteorSpawnInterval;
    }
}