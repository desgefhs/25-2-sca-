package org.newdawn.spaceinvaders.wave;

public interface WaveState {

    /**
     * Called when this state is entered.
     * @param waveManager The context (WaveManager).
     */
    void onEnter(WaveManager waveManager);

    /**
     * Called every frame to update the logic for this state.
     * @param waveManager The context (WaveManager).
     * @param delta Time since the last frame.
     */
    void update(WaveManager waveManager, long delta);

    /**
     * Called when this state is exited.
     * @param waveManager The context (WaveManager).
     */
    void onExit(WaveManager waveManager);
}
