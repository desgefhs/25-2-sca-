package org.newdawn.spaceinvaders.core;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.newdawn.spaceinvaders.auth.AuthManager;
import org.newdawn.spaceinvaders.auth.AuthenticatedUser;
import org.newdawn.spaceinvaders.auth.LoginDialog;
import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.wave.FormationManager;
import org.newdawn.spaceinvaders.sound.SoundManager;
import org.newdawn.spaceinvaders.player.PlayerManager;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.wave.WaveManager;
import org.newdawn.spaceinvaders.view.GameWindow;
import org.newdawn.spaceinvaders.view.MainMenu;
import org.newdawn.spaceinvaders.view.PauseMenu;
import org.newdawn.spaceinvaders.view.GameOverMenu;
import org.newdawn.spaceinvaders.view.Background;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;
import org.newdawn.spaceinvaders.view.ConfirmDialog;
import org.newdawn.spaceinvaders.view.UIManager;

import java.util.HashMap;
import java.util.Map;
import org.newdawn.spaceinvaders.core.GameStateManager;
import org.newdawn.spaceinvaders.gamestates.GameStateFactory;

import org.newdawn.spaceinvaders.data.DatabaseManager;


import java.io.IOException;
import java.io.InputStream;

// Firebase 초기화, 로그인 창 호출
public class Game {

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    public static final int GAME_WIDTH = 500;
    public static final int GAME_HEIGHT = 600;

    public static void main(String[] argv) {
        // Firebase 초기 화
        Firestore db = initializeFirebase();
        if (db == null) {
            System.err.println("Firebase 초기화 실패. 프로그램을 종료합니다.");
            return;
        }

        // 인증 관리자 생성
        AuthManager authManager = new AuthManager(db);

        // 로그인 다이얼로그 생성 및 표시
        LoginDialog loginDialog = new LoginDialog(null, authManager);
         AuthenticatedUser user = loginDialog.showDialog();

        //   로그인 성공 여부 확인
        if (user == null) {
            // 로그인 실패 또는 취소
            System.out.println("로그인이 취소되었습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }

        // 로그인 성공 시,  게임 시작
        System.out.println(user.getUsername() + "님, 환영합니다!");

        // 1. Create GameManager
        GameManager gameManager = new GameManager();

        // 2. Create Dependencies
        InputHandler inputHandler = new InputHandler();
        DatabaseManager databaseManager = new DatabaseManager(db);
        ShopManager shopManager = new ShopManager();
        FormationManager formationManager = new FormationManager();
        SoundManager soundManager = new SoundManager();
        WaveManager waveManager = new WaveManager(gameManager, formationManager);
        PlayerManager playerManager = new PlayerManager(user, databaseManager, shopManager, soundManager, waveManager);
        EntityManager entityManager = new EntityManager(gameManager);
        GameWindow gameWindow = new GameWindow(inputHandler);
        MainMenu mainMenu = new MainMenu();
        PauseMenu pauseMenu = new PauseMenu();
        GameOverMenu gameOverMenu = new GameOverMenu();
        Background background = new Background("sprites/gamebackground.png");
        Sprite staticBackgroundSprite = SpriteStore.get().getSprite("sprites/background.jpg");
        ConfirmDialog confirmDialog = new ConfirmDialog("Are you sure you want to exit?");
        UIManager uiManager = new UIManager(gameWindow, mainMenu, pauseMenu, gameOverMenu, confirmDialog, staticBackgroundSprite);
        GameStateManager gsm = new GameStateManager();
        GameStateFactory gameStateFactory = new GameStateFactory();

        GameContainer gameContainer = new GameContainer(databaseManager, playerManager, shopManager, soundManager,
                formationManager, waveManager, entityManager, uiManager, gsm, inputHandler);

        Map<String, org.newdawn.spaceinvaders.entity.weapon.Weapon> weapons = new HashMap<>();
        weapons.put("DefaultGun", new org.newdawn.spaceinvaders.entity.weapon.DefaultGun());
        weapons.put("Shotgun", new org.newdawn.spaceinvaders.entity.weapon.Shotgun());
        weapons.put("Laser", new org.newdawn.spaceinvaders.entity.weapon.Laser());

        // 3. Inject Dependencies into GameManager
        GameWorld gameWorld = new GameWorld(entityManager, background, waveManager, gameManager);
        gameManager.setGameWorld(gameWorld);
        gameManager.setGameContainer(gameContainer);
        gameManager.setGameStateFactory(gameStateFactory);
        gameManager.setWeapons(weapons);

        // 4. Initialize and Start Game
        gameManager.init();
        gameManager.setCurrentState(GameState.Type.MAIN_MENU);
        gameManager.initializePlayer();
        gameManager.startGame();
    }

    /**
     * Firebase Admin SDK를 초기화하고 Firestore 인스턴스를 반환합니다.
     * @return 초기화된 Firestore 인스턴스, 실패 시 null
     */
    private static Firestore initializeFirebase() {
        try {
            // 리소스 폴더에 있는 서비스 계정 키 파일을 읽어옵니다.
            InputStream serviceAccount = Game.class.getClassLoader().getResourceAsStream("serviceAccountKey.json");
            if (serviceAccount == null) {
                throw new IOException("serviceAccountKey.json 파일을 resources 폴더에서 찾을 수 없습니다.");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            // 앱이 이미 초기화되지 않았을 경우에만 초기화 진행
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            return FirestoreClient.getFirestore();
        } catch (IOException e) {
            System.err.println("Firebase 초기화 오류: " + e.getMessage() + ". 스택 트레이스를 확인하려면 디버그 모드를 사용하세요.");
            return null;
        }
    }
}
