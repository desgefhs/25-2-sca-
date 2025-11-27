package org.newdawn.spaceinvaders.wave;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link WaveDefinition} 객체를 생성하는 책임을 가진 클래스.
 * 각 웨이브가 무엇으로 구성되는지에 대한 로직을 캡슐화하여,
 * 웨이브 설정에 대한 데이터 소스 또는 팩토리 역할을 효과적으로 수행합니다.
 */
public class WaveLoader {

    /**
     * 지정된 웨이브 번호에 대한 웨이브 정의를 로드하고 반환합니다.
     *
     * @param waveNumber 로드할 웨이브의 번호.
     * @return 지정된 웨이브에 대한 {@link WaveDefinition} 객체. 웨이브 번호가 유효하지 않으면 null을 반환합니다.
     */
    public WaveDefinition loadWave(int waveNumber) {
        if (waveNumber <= 0 || waveNumber > 25) {
            return null; // 유효하지 않은 웨이브 번호
        }

        // 보스 웨이브는 매 5번째 웨이브마다 발생
        if (waveNumber % 5 == 0) {
            List<SpawnInfo> spawns = new ArrayList<>();
            // 보스의 경우, 'stage' 파라미터는 스케일링 목적으로 웨이브 번호를 나타낼 수 있음
            spawns.add(new SpawnInfo("BOSS", waveNumber, false, 0));
            return new WaveDefinition(waveNumber, "boss1", spawns);
        }

        // 일반 포메이션 웨이브
        String music = null;
        // 보스전 이후 새로운 스테이지가 시작될 때 기본 배경 음악으로 변경
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
            // 강제 업그레이드 여부는 스테이지에 따라 결정되도록 단순화
            boolean forceUpgrade = (stage >= 4);
            long delay = (i == 0) ? 0 : 3000L; // 첫 포메이션은 딜레이 없음
            spawns.add(new SpawnInfo("FORMATION", stage, forceUpgrade, delay));
        }

        return new WaveDefinition(waveNumber, music, spawns);
    }
}
