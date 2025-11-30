package org.newdawn.spaceinvaders.core;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.newdawn.spaceinvaders.auth.AuthManager;
import org.newdawn.spaceinvaders.auth.AuthenticatedUser;
import org.newdawn.spaceinvaders.auth.LoginDialog;

import java.io.IOException;
import java.io.InputStream;

/**
 * Space Invaders 게임 애플리케이션의 메인 진입점입니다.
 * Firebase 초기화, 사용자 인증, 게임 인스턴스 생성 및 전체 게임 루프의 시작을 담당합니다.
 */
public class Game {

    /** 전체 화면의 너비. */
    public static final int SCREEN_WIDTH = 800;
    /** 전체 화면의 높이. */
    public static final int SCREEN_HEIGHT = 600;
    /** 실제 게임 플레이가 이루어지는 영역의 너비. */
    public static final int GAME_WIDTH = 500;
    /** 실제 게임 플레이가 이루어지는 영역의 높이. */
    public static final int GAME_HEIGHT = 600;

    /**
     * 게임 애플리케이션의 주 실행 메소드.
     * @param argv 커맨드 라인 인수 (사용되지 않음)
     */
    public static void main(String[] argv) {
        // Firebase 초기화
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

        // 로그인 성공 여부 확인
        if (user == null) {
            // 로그인 실패 또는 취소
            System.out.println("로그인이 취소되었습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }

        // 로그인 성공 시, 게임 시작
        System.out.println(user.getUsername() + "님, 환영합니다!");

        // 팩토리를 사용하여 게임 인스턴스 생성
        GameFactory gameFactory = new GameFactory(db, user);
        GameManager gameManager = gameFactory.createGame();

        // 게임 초기화 및 시작
        gameManager.init();
        gameManager.setCurrentState(GameState.Type.MAIN_MENU);
        gameManager.initializePlayer();
        gameManager.startGame();
    }

    /**
     * Firebase Admin SDK를 초기화하고 Firestore 인스턴스를 반환합니다.
     * 리소스 폴더에서 `serviceAccountKey.json` 파일을 찾아 인증을 수행합니다.
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
            System.err.println("Firebase 초기화 오류: " + e.getMessage());
            return null;
        }
    }
}
