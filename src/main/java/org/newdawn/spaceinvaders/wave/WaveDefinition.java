package org.newdawn.spaceinvaders.wave;

import java.util.List;

/**
 * A data class that holds all the defining information for a single wave.
 * This object is used by the WaveManager to execute the wave, separating the "what" from the "how".
 */
public class WaveDefinition {

    private final int waveNumber;
    private final String music;
    private final List<SpawnInfo> spawns;

    public WaveDefinition(int waveNumber, String music, List<SpawnInfo> spawns) {
        this.waveNumber = waveNumber;
        this.music = music;
        this.spawns = spawns;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public String getMusic() {
        return music;
    }

    public List<SpawnInfo> getSpawns() {
        return spawns;
    }
}
