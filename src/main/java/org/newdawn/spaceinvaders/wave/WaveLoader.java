package org.newdawn.spaceinvaders.wave;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for creating WaveDefinition objects.
 * It encapsulates the logic of what each wave consists of, effectively acting
 * as a data source or factory for wave configurations.
 */
public class WaveLoader {

    /**
     * Loads and returns the definition for a specific wave number.
     *
     * @param waveNumber The number of the wave to load.
     * @return A WaveDefinition object for the specified wave, or null if the wave number is invalid.
     */
    public WaveDefinition loadWave(int waveNumber) {
        if (waveNumber <= 0 || waveNumber > 25) {
            return null; // Invalid wave number
        }

        // Boss waves occur every 5th wave
        if (waveNumber % 5 == 0) {
            List<SpawnInfo> spawns = new ArrayList<>();
            // For a boss, the 'stage' parameter can represent the wave number for scaling purposes.
            spawns.add(new SpawnInfo("BOSS", waveNumber, false, 0));
            return new WaveDefinition(waveNumber, "boss1", spawns);
        }

        // Regular formation waves
        String music = null;
        // Music changes to the default background music at the start of a new stage (after a boss)
        if ((waveNumber - 1) % 5 == 0 || waveNumber == 1) {
            music = "gamebackground";
        }

        int stage = ((waveNumber - 1) / 5) + 1;
        int formationsPerWave;
        switch (stage) {
            case 1: formationsPerWave = 3; break;
            case 2: formationsPerWave = 4; break;
            case 3: formationsPerWave = 5; break;
            case 4: formationsPerWave = 6; break;
            case 5: formationsPerWave = 7; break;
            default: formationsPerWave = 3; break;
        }

        List<SpawnInfo> spawns = new ArrayList<>();
        for (int i = 0; i < formationsPerWave; i++) {
            // NOTE: The original logic for 'forceUpgrade' depended on random formation names.
            // This has been simplified to be stage-dependent for this refactoring.
            boolean forceUpgrade = (stage >= 4);
            long delay = (i == 0) ? 0 : 3000L; // No delay for the first formation
            spawns.add(new SpawnInfo("FORMATION", stage, forceUpgrade, delay));
        }

        return new WaveDefinition(waveNumber, music, spawns);
    }
}
