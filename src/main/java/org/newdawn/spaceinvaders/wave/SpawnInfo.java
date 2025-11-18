package org.newdawn.spaceinvaders.wave;

/**
 * Represents a single spawn event within a wave.
 * This is a data class used to define what to spawn and when.
 */
public class SpawnInfo {

    /**
     * The type of entity or group to spawn.
     * Can be a generic type like "FORMATION" or a specific boss like "BOSS_WAVE_5".
     */
    private final String type;

    /**
     * For formation spawns, this indicates the stage to use for selecting a random formation.
     * For other spawn types, this can be 0.
     */
    private final int stage;

    /**
     * A flag to indicate if the spawned enemies should be forcibly upgraded.
     */
    private final boolean forceUpgrade;

    /**
     * The delay in milliseconds before this spawn event occurs, relative to the previous event.
     */
    private final long delay;

    public SpawnInfo(String type, int stage, boolean forceUpgrade, long delay) {
        this.type = type;
        this.stage = stage;
        this.forceUpgrade = forceUpgrade;
        this.delay = delay;
    }

    public String getType() {
        return type;
    }

    public int getStage() {
        return stage;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }

    public long getDelay() {
        return delay;
    }
}
