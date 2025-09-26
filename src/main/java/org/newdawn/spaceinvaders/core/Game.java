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
 * 게임의 주 진입점(Entry Point) 역할을 하는 클래스.
 * Firebase 초기화, 로그인 창 호출, GameManager 시작을 담당합니다.
 */
public class Game {

    public static void main(String[] argv) {
        // 1. Firebase 초기화
        Firestore db = initializeFirebase();
        if (db == null) {
            System.err.println("Firebase 초기화 실패. 프로그램을 종료합니다.");
            return;
        }

        // 2. 인증 관리자 생성
        AuthManager authManager = new AuthManager(db);

        // 3. 로그인 다이얼로그 생성 및 표시
        LoginDialog loginDialog = new LoginDialog(null, authManager);
         AuthenticatedUser user = loginDialog.showDialog();

        // 4. 로그인 성공 여부 확인
        if (user == null) {
            // 로그인 실패 또는 취소 시 프로그램 종료
            System.out.println("로그인이 취소되었습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }

        // 5. 로그인 성공 시, 사용자 정보와 함께 GameManager 생성 및 게임 시작
        System.out.println(user.getUsername() + "님, 환영합니다!");
        GameManager gameManager = new GameManager(user, db);
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
