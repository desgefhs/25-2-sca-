package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.player.PlayerManager;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.sound.SoundManager;
import org.newdawn.spaceinvaders.wave.FormationManager;
import org.newdawn.spaceinvaders.wave.WaveManager;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.view.UIManager;

/**
 * 게임의 주요 관리자(Manager) 클래스들의 인스턴스를 보관하는 컨테이너 클래스.
 * 의존성 주입(Dependency Injection) 컨테이너 역할을 하여, 게임 전체에서
 * 각 관리자 객체에 대한 중앙 접근점을 제공합니다.
 */
public class GameContainer {

    private final DatabaseManager databaseManager;
    private final PlayerManager playerManager;
    private final ShopManager shopManager;
    private final SoundManager soundManager;
    private final FormationManager formationManager;
    private final WaveManager waveManager;
    private final EntityManager entityManager;
    private final UIManager uiManager;
    private final GameStateManager gsm;
    private final InputHandler inputHandler;

    /**
     * GameContainer 생성자.
     * 게임의 모든 주요 관리자 객체들을 주입받아 초기화합니다.
     *
     * @param databaseManager 데이터베이스 매니저
     * @param playerManager 플레이어 매니저
     * @param shopManager 상점 매니저
     * @param soundManager 사운드 매니저
     * @param formationManager 포메이션 매니저
     * @param waveManager 웨이브 매니저
     * @param entityManager 엔티티 매니저
     * @param uiManager UI 매니저
     * @param gsm 게임 상태 매니저
     * @param inputHandler 입력 핸들러
     */
    public GameContainer(DatabaseManager databaseManager, PlayerManager playerManager, ShopManager shopManager,
                         SoundManager soundManager, FormationManager formationManager, WaveManager waveManager,
                         EntityManager entityManager, UIManager uiManager, GameStateManager gsm, InputHandler inputHandler) {
        this.databaseManager = databaseManager;
        this.playerManager = playerManager;
        this.shopManager = shopManager;
        this.soundManager = soundManager;
        this.formationManager = formationManager;
        this.waveManager = waveManager;
        this.entityManager = entityManager;
        this.uiManager = uiManager;
        this.gsm = gsm;
        this.inputHandler = inputHandler;
    }

    // 각 관리자 객체에 대한 Getter 메소드들
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public ShopManager getShopManager() { return shopManager; }
    public SoundManager getSoundManager() { return soundManager; }
    public FormationManager getFormationManager() { return formationManager; }
    public WaveManager getWaveManager() { return waveManager; }
    public EntityManager getEntityManager() { return entityManager; }
    public UIManager getUiManager() { return uiManager; }
    public GameStateManager getGsm() { return gsm; }
    public InputHandler getInputHandler() { return inputHandler; }
}
