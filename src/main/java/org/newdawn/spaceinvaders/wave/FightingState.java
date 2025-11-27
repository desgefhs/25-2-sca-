package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity;
import org.newdawn.spaceinvaders.entity.HealingAreaEntity;

/**
 * 플레이어가 적과 싸우는 '전투 중' 상태를 구현한 클래스.
 * 이 상태에서는 메테오와 힐링 영역 스폰을 처리하고, 모든 적이 처치되었는지 확인하여 웨이브 종료를 감지합니다.
 */
public class FightingState implements WaveState {

    /**
     * 전투 상태에 진입할 때 호출됩니다.
     * @param waveManager 컨텍스트 역할을 하는 WaveManager
     */
    @Override
    public void onEnter(WaveManager waveManager) {
        // 일반적인 전투 단계가 시작됩니다.
    }

    /**
     * 전투 상태의 로직을 매 프레임 업데이트합니다.
     * 메테오 및 힐링 영역 스폰을 처리하고, 웨이브 클리어 조건을 확인합니다.
     * @param waveManager 컨텍스트 역할을 하는 WaveManager
     * @param delta 마지막 프레임 이후 경과된 시간
     */
    @Override
    public void update(WaveManager waveManager, long delta) {
        // 이 상태는 이제 이 로직을 직접 처리합니다.
        handleMeteorSpawning(waveManager);
        handleHealingAreaSpawning(waveManager);

        // 모든 외계인이 처치되었는지 확인하여 웨이브를 종료합니다.
        if (waveManager.getGameManager().getGameContainer().getEntityManager().getAlienCount() <= 0) {
            // 새로운 상태로 변경하는 대신, 다음 웨이브 로직을 직접 트리거합니다.
            waveManager.getGameManager().onWaveCleared();
        }
    }

    /**
     * 전투 상태를 벗어날 때 호출됩니다. (예: 새로운 웨이브 시작 시)
     * @param waveManager 컨텍스트 역할을 하는 WaveManager
     */
    @Override
    public void onExit(WaveManager waveManager) {
        // 새로운 웨이브가 시작될 때 호출됩니다.
    }

    /**
     * 무작위 메테오 스폰 로직을 처리합니다.
     * @param waveManager 컨텍스트 역할을 하는 WaveManager
     */
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

    /**
     * 특정 조건에 따라 힐링 영역 스폰 로직을 처리합니다.
     * @param waveManager 컨텍스트 역할을 하는 WaveManager
     */
    private void handleHealingAreaSpawning(WaveManager waveManager) {
        if (waveManager.getWave() > 0 && waveManager.getWave() % 8 == 0 && !waveManager.isHealingAreaSpawnedForWave()) {
            int xPos = (int) (Math.random() * (Game.GAME_WIDTH - 50));
            waveManager.getGameManager().addEntity(new HealingAreaEntity(waveManager.getGameManager(), xPos, -50));
            waveManager.setHealingAreaSpawnedForWave(true);
        }
    }
}
