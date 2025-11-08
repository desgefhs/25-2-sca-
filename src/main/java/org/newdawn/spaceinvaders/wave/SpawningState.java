package org.newdawn.spaceinvaders.wave;

public class SpawningState implements WaveState {
    
    @Override
    public void onEnter(WaveManager waveManager) {
        // This state is entered right after a wave starts.
        // The initial formation is already spawned by startNextWave(),
        // so we just need to manage subsequent spawns.
    }

    @Override
    public void update(WaveManager waveManager, long delta) {
        // This is the logic moved from the old WaveManager.handleSpawning()
        if (waveManager.getFormationsSpawnedInWave() < waveManager.getFormationsPerWave() &&
            waveManager.getNextFormationSpawnTime() > 0 &&
            System.currentTimeMillis() > waveManager.getNextFormationSpawnTime()) {
            
            waveManager.spawnNextFormationInWave();
        }

        // If all formations for the current wave have been spawned, transition to the FightingState.
        // This also applies to boss waves, which have only 1 formation.
        if (waveManager.getFormationsSpawnedInWave() >= waveManager.getFormationsPerWave()) {
            waveManager.setState(new FightingState());
        }
    }

    @Override
    public void onExit(WaveManager waveManager) {
        // No specific cleanup needed when leaving the spawning phase.
    }
}
