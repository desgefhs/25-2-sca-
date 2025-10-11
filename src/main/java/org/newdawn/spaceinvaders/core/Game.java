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
 * 게임의 주 클래스입니다.
 * Firebase 초기화, 사용자 로그인, 게임 시작
 */
public class Game {

    // 화면 너비
    public static final int SCREEN_WIDTH = 800;
    // 화면 높이
    public static final int SCREEN_HEIGHT = 600;
    //게임 영역 너비
    public static final int GAME_WIDTH = 500;
    //게임 영역 높이
    public static final int GAME_HEIGHT = 600;

    // 주실행 메서드
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

        //   로그인 성공 여부 확인
        if (user == null) {
            // 로그인 실패 또는 취소
            System.out.println("로그인이 취소되었습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }

        // 로그인 성공 시,  게임 시작
        System.out.println(user.getUsername() + "님, 환영합니다!");
        GameManager gameManager = new GameManager(user, db);
        gameManager.initializePlayer();
        gameManager.startGame();
    }

    /**
     * Firebase Admin SDK 초기화
     * @return 초기화된 Firestore 인스턴스, 실패 시 null
     */
    private static Firestore initializeFirebase() {
        try {
            // 리소스 폴더에 있는 서비스 계정 키 파일 가져오기
            InputStream serviceAccount = Game.class.getClassLoader().getResourceAsStream("serviceAccountKey.json");
            if (serviceAccount == null) {
                throw new IOException("serviceAccountKey.json 파일을 resources 폴더에서 찾을 수 없습니다.");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            // 앱이 이미 초기화되지 않았을 경우에만 초기화 진행
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            return FirestoreClient.getFirestore();
        } catch (IOException e) {
            System.err.println("Firebase 초기화 오류: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
