
package org.newdawn.spaceinvaders.sound;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private final Map<String, Clip> clips = new HashMap<>();

    public SoundManager() {
        // Load sounds into memory
        loadSound("gamebackground", "/sounds/GameBackground.wav");
        loadSound("gunshot", "/sounds/gunshot.wav");
        loadSound("lasershot", "/sounds/lasershot.wav");
        loadSound("shotgun", "/sounds/shotgun.wav");
        loadSound("ship-death-sound", "/sounds/ship-death-sound.wav");
        loadSound("boss1", "/sounds/boss1.wav");
        loadSound("buttonselect", "/sounds/buttonselect.wav");
        loadSound("menubackground", "/sounds/MainmenuBackground.wav");
    }

    private void loadSound(String name, String path) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(path);
            if (audioSrc == null) {
                System.err.println("Can't find sound file: " + path);
                return;
            }
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clips.put(name, clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSound(String name) {
        Clip clip = clips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                return;
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void loopSound(String name) {
        Clip clip = clips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                return;
            }
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopSound(String name) {
        Clip clip = clips.get(name);
        if (clip != null) {
            clip.stop();
        }
    }

    public void stopAllSounds(String exclude) {
        for (Map.Entry<String, Clip> entry : clips.entrySet()) {
            if (!entry.getKey().equals(exclude)) {
                entry.getValue().stop();
            }
        }
    }

    public void stopAllSounds() {
        stopAllSounds(null);
    }
}
