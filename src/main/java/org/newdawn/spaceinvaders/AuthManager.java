package org.newdawn.spaceinvaders;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Firebase  관련된 모든 로직을 처리
 */
public class AuthManager {

    //SDK를 초기화
    public void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream("C:\\Users\\jiho_\\Desktop\\지호\\프로젝트\\25-2-sca-\\src\\main\\resources\\serviceAccountKey.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { // 앱이 이미 초기화되었는지 확인
                FirebaseApp.initializeApp(options);
            }
            System.out.println("Firebase Admin SDK가 성공적으로 초기화되었습니다.");
        } catch (IOException e) {
            System.err.println("Firebase 초기화 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 이메일과 비밀번호로 새로운 사용자를 등록합니다.
     * @param email 사용자 이메일 (ID 역할)
     * @param password 사용자 비밀번호
     * @return 등록 성공 시 true, 실패 시 false
     */
    public boolean register(String email, String password) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("성공적으로 새로운 사용자 등록: " + userRecord.getUid());
            return true;
        } catch (Exception e) {
            System.err.println("사용자 등록 오류: " + e.getMessage());
            return false;
        }
    }

    /**
     * 이메일과 비밀번호로 로그인을 시도합니다.
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return 로그인 성공 시 true, 실패 시 false
     */
    public boolean login(String email, String password) {
        try {
            // Firebase Admin SDK는 주로 서버 환경에서 사용되므로, 클라이언트 측 로그인 확인은
            // 보통 클라이언트 SDK(Android, iOS, Web)에서 토큰을 받아와 검증하는 방식을 사용합니다.
            // 여기서는 이메일로 사용자를 조회하여 존재하는지만 확인하는 방식으로 로그인을 간소화합니다.
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            // 비밀번호 확인 로직은 Admin SDK에 직접적으로 없으므로, 실제로는 클라이언트에서 로그인 후 토큰을 받아야 합니다.
            // 여기서는 사용자가 존재하면 로그인 성공으로 간주하는 테스트용 로직입니다.
            System.out.println("사용자 로그인 성공: " + userRecord.getUid());
            return true;
        } catch (Exception e) {
            System.err.println("로그인 오류 또는 사용자가 존재하지 않음: " + e.getMessage());
            return false;
        }
    }
}
