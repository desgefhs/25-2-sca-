package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.BossFactory;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.events.GameWonEvent;

/**
 * 게임 내 적 웨이브의 진행을 책임지는 클래스입니다.
 * 이 클래스는 "Wave Executor" 역할을 하도록 리팩토링되었습니다. {@link WaveLoader}를 사용하여
 * 웨이브 정의를 가져오고, {@link WaveDefinition}에 기술된 스폰 이벤트를 순차적으로 실행합니다.
 * 이를 통해 웨이브 데이터(무엇을)와 실행 로직(어떻게)이 분리됩니다.
 */
public class WaveManager {

    /** 보스전 배경 음악 이름. */
    private static final String BOSS_MUSIC = "boss1";
    /** 게임의 중앙 관리자. */
    private final GameManager gameManager;
    /** 포메이션 데이터를 관리하는 매니저. */
    private final FormationManager formationManager;
    /** 웨이브 정의 파일을 로드하는 로더. */
    private final WaveLoader waveLoader;

    /** 현재 웨이브 번호. */
    private int wave = 0;
    /** 현재 웨이브에서 힐링 영역이 스폰되었는지 여부. */
    private boolean healingAreaSpawnedForWave = false;

    /** 현재 실행 중인 웨이브의 정의. */
    private WaveDefinition currentWaveDefinition;
    /** 현재 웨이브 정의에서 실행할 스폰 이벤트의 인덱스. */
    private int currentSpawnIndex;
    /** 다음 스폰 이벤트가 실행될 시간 (타임스탬프). */
    private long nextSpawnTime;

    // 운석 스폰 관련 필드 - 여기에서 완전히 처리되지 않은 별도의 로직의 일부로 보임.
    private long lastMeteorSpawnTime;
    private long nextMeteorSpawnInterval;

    /**
     * WaveManager 생성자.
     * @param gameManager 게임 중앙 관리자
     * @param formationManager 포메이션 매니저
     */
    public WaveManager(GameManager gameManager, FormationManager formationManager) {
        this.gameManager = gameManager;
        this.formationManager = formationManager;
        this.waveLoader = new WaveLoader(); // 로더를 직접 인스턴스화
    }

    /**
     * 타이머를 초기화합니다. 게임 플레이 상태에 진입할 때 호출됩니다.
     */
    public void init() {
        lastMeteorSpawnTime = System.currentTimeMillis();
        nextMeteorSpawnInterval = 1000 + (long) (Math.random() * 1000);
    }

    /**
     * WaveManager의 메인 업데이트 루프.
     * 현재 웨이브 정의에 따라 다음 스폰 이벤트를 실행할 시간인지 확인합니다.
     * @param delta 마지막 프레임 이후 경과 시간
     */
    public void update(long delta) {
        if (currentWaveDefinition == null || currentSpawnIndex >= currentWaveDefinition.getSpawns().size()) {
            // 웨이브가 비활성화되었거나 모든 스폰 이벤트가 완료됨.
            return;
        }

        if (System.currentTimeMillis() >= nextSpawnTime) {
            executeCurrentSpawn();
        }
    }

    /**
     * 현재 인덱스에 해당하는 스폰 이벤트를 실행합니다.
     */
    private void executeCurrentSpawn() {
        SpawnInfo spawn = currentWaveDefinition.getSpawns().get(currentSpawnIndex);

        // 타입에 따라 스폰 실행
        if ("FORMATION".equals(spawn.getType())) {
            spawnFormation(spawn);
        } else if ("BOSS".equals(spawn.getType())) {
            spawnBoss(spawn);
        }

        currentSpawnIndex++;

        // 다음 스폰 이벤트 예약
        if (currentSpawnIndex < currentWaveDefinition.getSpawns().size()) {
            SpawnInfo nextSpawn = currentWaveDefinition.getSpawns().get(currentSpawnIndex);
            nextSpawnTime = System.currentTimeMillis() + nextSpawn.getDelay();
        } else {
            // 이 웨이브의 모든 스폰이 끝남. 업데이트를 멈추기 위해 정의를 null로 설정.
            currentWaveDefinition = null;
        }
    }

    /**
     * 주어진 SpawnInfo에 따라 포메이션을 스폰합니다.
     * @param spawn 포메이션에 대한 스폰 정보
     */
    private void spawnFormation(SpawnInfo spawn) {
        Formation formation = formationManager.getRandomFormationForStage(spawn.getStage());
        gameManager.getGameContainer().getEntityManager().spawnFormation(formation, wave, spawn.isForceUpgrade());
    }

    /**
     * 주어진 SpawnInfo에 따라 보스를 스폰합니다.
     * @param spawn 보스에 대한 스폰 정보
     */
    private void spawnBoss(SpawnInfo spawn) {
        // 보스전 전에 플레이어 함선을 제외한 모든 엔티티 제거.
        gameManager.getGameContainer().getEntityManager().getEntities().removeIf(entity -> !(entity instanceof ShipEntity));

        int waveNumberForBoss = spawn.getStage(); // 스테이지 필드를 보스 웨이브 번호로 재사용
        int cycle = (waveNumberForBoss - 1) / 5;
        double cycleMultiplier = Math.pow(1.5, cycle);
        int bossHealth = (int) (50 * cycleMultiplier);
        Entity boss = BossFactory.createBoss(gameManager, waveNumberForBoss, Game.GAME_WIDTH / 2, 50, bossHealth);
        gameManager.addEntity(boss);
        gameManager.getGameContainer().getEntityManager().setAlienCount(1);
    }

    /**
     * 첫 번째 웨이브를 시작합니다.
     */
    public void startFirstWave() {
        wave = 0;
        startNextWave();
    }

    /**
     * 디버깅용 메소드로, 다음 보스 웨이브 직전으로 건너뜁니다.
     */
    public void skipToNextBossWave() {
        this.wave = ((this.wave / 5) * 5) + 4; // 다음 보스 웨이브 바로 전 웨이브로 설정
        startNextWave();
    }

    /**
     * 다음 웨이브를 시작합니다. 웨이브 정의를 로드하고 첫 스폰을 예약합니다.
     */
    public void startNextWave() {
        wave++;
        healingAreaSpawnedForWave = false;

        this.currentWaveDefinition = waveLoader.loadWave(wave);

        if (currentWaveDefinition == null) {
            if (wave > 25) { // 승리 조건
                gameManager.getEventBus().publish(new GameWonEvent());
            }
            return;
        }

        gameManager.setMessage("Wave " + wave);
        gameManager.setMessageEndTime(System.currentTimeMillis() + 1000);

        // 웨이브에 정의된 음악 설정
        if (currentWaveDefinition.getMusic() != null) {
            if (currentWaveDefinition.getMusic().equals(BOSS_MUSIC)) {
                gameManager.getSoundManager().stopSound("gamebackground");
                gameManager.getSoundManager().loopSound(BOSS_MUSIC);
            } else {
                gameManager.getSoundManager().stopSound(BOSS_MUSIC);
                gameManager.getSoundManager().loopSound("gamebackground");
            }
        }

        // 스폰 상태 리셋 및 첫 스폰 예약
        this.currentSpawnIndex = 0;
        if (!currentWaveDefinition.getSpawns().isEmpty()) {
            this.nextSpawnTime = System.currentTimeMillis() + currentWaveDefinition.getSpawns().get(0).getDelay();
        }

        gameManager.setCurrentState(GameState.Type.PLAYING);
    }

    /**
     * 현재 웨이브의 모든 적 스폰이 완료되었는지 확인합니다.
     * @return 모든 스폰이 완료되었으면 true, 그렇지 않으면 false
     */
    public boolean hasFinishedSpawning() {
        // 활성화된 웨이브 정의가 없으면 스폰이 끝난 것으로 간주.
        if (currentWaveDefinition == null) {
            return true;
        }
        // 그렇지 않으면 모든 스폰 이벤트가 실행되었는지 확인.
        return currentSpawnIndex >= currentWaveDefinition.getSpawns().size();
    }

    // --- Getter 및 Setter 메소드 ---

    /**
     * 현재 웨이브 번호를 반환합니다.
     * @return 현재 웨이브 번호
     */
    public int getWave() {
        return wave;
    }

    /**
     * 현재 웨이브에서 힐링 영역이 스폰되었는지 여부를 반환합니다.
     * @return 힐링 영역이 스폰되었으면 true
     */
    public boolean isHealingAreaSpawnedForWave() {
        return healingAreaSpawnedForWave;
    }

    /**
     * 현재 웨이브의 힐링 영역 스폰 상태를 설정합니다.
     * @param healingAreaSpawnedForWave 힐링 영역 스폰 여부
     */
    public void setHealingAreaSpawnedForWave(boolean healingAreaSpawnedForWave) {
        this.healingAreaSpawnedForWave = healingAreaSpawnedForWave;
    }

    /**
     * 게임 관리자 인스턴스를 반환합니다.
     * @return 게임 관리자
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * 마지막 운석이 스폰된 시간을 반환합니다.
     * @return 마지막 운석 스폰 시간
     */
    public long getLastMeteorSpawnTime() {
        return lastMeteorSpawnTime;
    }

    /**
     * 마지막 운석 스폰 시간을 설정합니다.
     * @param lastMeteorSpawnTime 마지막 운석 스폰 시간
     */
    public void setLastMeteorSpawnTime(long lastMeteorSpawnTime) {
        this.lastMeteorSpawnTime = lastMeteorSpawnTime;
    }

    /**
     * 다음 운석 스폰까지의 간격을 반환합니다.
     * @return 다음 운석 스폰 간격
     */
    public long getNextMeteorSpawnInterval() {
        return nextMeteorSpawnInterval;
    }

    /**
     * 다음 운석 스폰까지의 간격을 설정합니다.
     * @param nextMeteorSpawnInterval 다음 운석 스폰 간격
     */
    public void setNextMeteorSpawnInterval(long nextMeteorSpawnInterval) {
        this.nextMeteorSpawnInterval = nextMeteorSpawnInterval;
    }
}