package org.newdawn.spaceinvaders.core;

import com.google.cloud.firestore.Firestore;
import org.newdawn.spaceinvaders.auth.AuthenticatedUser;
import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.sound.SoundManager;
import org.newdawn.spaceinvaders.player.PlayerManager;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.wave.WaveManager;
import org.newdawn.spaceinvaders.wave.FormationManager;
import org.newdawn.spaceinvaders.view.GameWindow;
import org.newdawn.spaceinvaders.view.MainMenu;
import org.newdawn.spaceinvaders.view.PauseMenu;
import org.newdawn.spaceinvaders.view.GameOverMenu;
import org.newdawn.spaceinvaders.view.Background;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;
import org.newdawn.spaceinvaders.view.ConfirmDialog;
import org.newdawn.spaceinvaders.view.UIManager;
import org.newdawn.spaceinvaders.gamestates.GameStateFactory;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.entity.weapon.DefaultGun;
import org.newdawn.spaceinvaders.entity.weapon.Shotgun;
import org.newdawn.spaceinvaders.entity.weapon.Laser;

import java.util.HashMap;
import java.util.Map;

/**
 * 게임에 필요한 모든 객체를 생성하고 의존성을 주입하여 조립하는 팩토리 클래스.
 * 게임의 전체 객체 그래프를 구성하는 책임을 가집니다.
 */
public class GameFactory {

    /** Firestore 데이터베이스 인스턴스. */
    private final Firestore db;
    /** 현재 인증된 사용자 정보. */
    private final AuthenticatedUser user;

    /**
     * GameFactory 생성자.
     * @param db Firestore 데이터베이스 인스턴스
     * @param user 인증된 사용자 객체
     */
    public GameFactory(Firestore db, AuthenticatedUser user) {
        this.db = db;
        this.user = user;
    }

    /**
     * 모든 관리자 및 구성 요소를 생성하고 의존성을 주입하여
     * 완전히 구성된 {@link GameManager} 인스턴스를 생성합니다.
     *
     * @return 의존성 주입이 완료된 GameManager 인스턴스
     */
    public GameManager createGame() {
        // 1. 관리자 및 핵심 구성 요소 생성
        GameManager gameManager = new GameManager();
        InputHandler inputHandler = new InputHandler();
        DatabaseManager databaseManager = new DatabaseManager(db);
        ShopManager shopManager = new ShopManager();
        FormationManager formationManager = new FormationManager();
        SoundManager soundManager = new SoundManager();
        WaveManager waveManager = new WaveManager(gameManager, formationManager);
        PlayerManager playerManager = new PlayerManager(user, databaseManager, shopManager, soundManager, waveManager);
        EntityManager entityManager = new EntityManager(gameManager);
        GameStateManager gsm = new GameStateManager();
        GameStateFactory gameStateFactory = new GameStateFactory();
        EntityLifecycleManager entityLifecycleManager = new EntityLifecycleManager();

        // 2. UI 구성 요소 생성
        GameWindow gameWindow = new GameWindow(inputHandler);
        MainMenu mainMenu = new MainMenu();
        PauseMenu pauseMenu = new PauseMenu();
        GameOverMenu gameOverMenu = new GameOverMenu();
        ConfirmDialog confirmDialog = new ConfirmDialog("Are you sure you want to exit?");
        Sprite staticBackgroundSprite = SpriteStore.get().getSprite("sprites/background.jpg");
        UIManager uiManager = new UIManager(gameWindow, mainMenu, pauseMenu, gameOverMenu, confirmDialog, staticBackgroundSprite);

        // 3. 게임 세계 및 컨테이너 생성
        GameContainer gameContainer = new GameContainer(databaseManager, playerManager, shopManager, soundManager,
                formationManager, waveManager, entityManager, uiManager, gsm, inputHandler);
        Background background = new Background("sprites/gamebackground.png");
        GameWorld gameWorld = new GameWorld(entityManager, background, waveManager, gameManager, entityLifecycleManager);

        // 4. 무기 생성
        Map<String, Weapon> weapons = new HashMap<>();
        weapons.put("DefaultGun", new DefaultGun());
        weapons.put("Shotgun", new Shotgun());
        weapons.put("Laser", new Laser());

        // 5. GameManager에 의존성 주입
        gameManager.setGameWorld(gameWorld);
        gameManager.setGameContainer(gameContainer);
        gameManager.setGameStateFactory(gameStateFactory);
        gameManager.setWeapons(weapons);

        return gameManager;
    }
}