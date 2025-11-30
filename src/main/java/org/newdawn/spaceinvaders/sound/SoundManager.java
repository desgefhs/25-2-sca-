
package org.newdawn.spaceinvaders.sound;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 게임 내 모든 사운드(효과음, 배경 음악)의 로딩과 제어를 담당하는 클래스입니다.
 * Java Sound API의 {@link Clip}을 사용하여 사운드를 메모리에 미리 로드하고 재생, 반복, 중지 기능을 제공합니다.
 */
public class SoundManager {

    /** 사운드 이름을 키로, Clip 객체를 값으로 저장하는 맵. */
    private final Map<String, Clip> clips = new HashMap<>();

    /**
     * SoundManager 생성자.
     * 게임에 필요한 모든 사운드를 메모리로 로드합니다.
     */
    public SoundManager() {
        // 사운드를 메모리에 로드
        loadSound("gamebackground", "/sounds/GameBackground.wav");
        loadSound("gunshot", "/sounds/gunshot.wav");
        loadSound("lasershot", "/sounds/lasershot.wav");
        loadSound("shotgun", "/sounds/shotgun.wav");
        loadSound("ship-death-sound", "/sounds/ship-death-sound.wav");
        loadSound("boss1", "/sounds/boss1.wav");
        loadSound("buttonselect", "/sounds/buttonselect.wav");
        loadSound("menubackground", "/sounds/MainmenuBackground.wav");
    }

    /**
     * 지정된 경로의 사운드 파일을 로드하여 맵에 저장합니다.
     *
     * @param name 사운드를 식별할 이름
     * @param path 사운드 파일의 리소스 경로
     */
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

    /**
     * 지정된 이름의 사운드를 한 번 재생합니다.
     * 이미 재생 중인 경우 다시 시작하지 않습니다.
     *
     * @param name 재생할 사운드의 이름
     */
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

    /**
     * 지정된 이름의 사운드를 무한 반복하여 재생합니다.
     * 이미 재생 중인 경우 다시 시작하지 않습니다.
     *
     * @param name 반복 재생할 사운드의 이름
     */
    public void loopSound(String name) {
        Clip clip = clips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                return;
            }
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * 지정된 이름의 사운드 재생을 중지합니다.
     *
     * @param name 중지할 사운드의 이름
     */
    public void stopSound(String name) {
        Clip clip = clips.get(name);
        if (clip != null) {
            clip.stop();
        }
    }

    /**
     * 특정 사운드를 제외한 모든 사운드의 재생을 중지합니다.
     *
     * @param exclude 중지하지 않을 사운드의 이름
     */
    public void stopAllSounds(String exclude) {
        for (Map.Entry<String, Clip> entry : clips.entrySet()) {
            if (!entry.getKey().equals(exclude)) {
                entry.getValue().stop();
            }
        }
    }

    /**
     * 현재 재생 중인 모든 사운드를 중지합니다.
     */
    public void stopAllSounds() {
        stopAllSounds(null);
    }
}
