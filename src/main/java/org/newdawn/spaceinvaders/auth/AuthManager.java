package org.newdawn.spaceinvaders.auth;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 사용자 인증(회원가입, 로그인)을 처리하는 클래스.
 * Firestore 데이터베이스와 통신하고, jBCrypt를 사용하여 비밀번호를 해싱하고 검증합니다.
 */
public class AuthManager {

    /** Firestore 'users' 컬렉션에서 사용자 이름을 나타내는 필드 키. */
    private static final String USERNAME_KEY = "username";
    /** Firestore 'users' 컬렉션에서 해시된 비밀번호를 나타내는 필드 키. */
    private static final String HASHED_PASSWORD_KEY = "hashedPassword";

    /** Firestore 데이터베이스 인스턴스. */
    private final Firestore db;
    /** 'users' 컬렉션에 대한 참조. */
    private final CollectionReference usersCollection;

    /**
     * AuthManager 생성자.
     * @param db Firestore 데이터베이스 인스턴스
     */
    public AuthManager(Firestore db) {
        this.db = db;
        this.usersCollection = db.collection("users");
    }

    /**
     * 새로운 사용자를 등록(회원가입)합니다.
     * 사용자 이름이 이미 존재하는지 확인하고, 비밀번호를 해싱하여 Firestore에 저장합니다.
     *
     * @param username 가입할 사용자 이름
     * @param password 가입할 비밀번호
     * @return 가입 성공 시 true, 실패(중복 이름 등) 시 false
     */
    public boolean signUp(String username, String password) {
        try {
            // 사용자 이름이 이미 존재하는지 확인
            ApiFuture<QuerySnapshot> future = usersCollection.whereEqualTo(USERNAME_KEY, username).get();
            QuerySnapshot snapshot = future.get();
            if (!snapshot.isEmpty()) {
                System.err.println("Username already exists.");
                return false;
            }

            // 비밀번호 해싱
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // 새 사용자 문서 생성
            Map<String, Object> user = new HashMap<>();
            user.put(USERNAME_KEY, username);
            user.put(HASHED_PASSWORD_KEY, hashedPassword);

            usersCollection.add(user).get();
            return true;

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 사용자를 로그인(인증)합니다.
     * 제공된 사용자 이름과 비밀번호를 데이터베이스의 기록과 비교하여 검증합니다.
     *
     * @param username 로그인할 사용자 이름
     * @param password 로그인할 비밀번호
     * @return 로그인 성공 시 사용자의 고유 문서 ID(UID), 실패 시 null
     */
    public String signIn(String username, String password) {
        try {
            // 사용자 이름으로 사용자 찾기
            ApiFuture<QuerySnapshot> future = usersCollection.whereEqualTo(USERNAME_KEY, username).limit(1).get();
            QuerySnapshot snapshot = future.get();

            if (snapshot.isEmpty()) {
                System.err.println("User not found.");
                return null;
            }

            // 사용자 문서 가져오기
            DocumentSnapshot userDoc = snapshot.getDocuments().get(0);
            String hashedPasswordFromDB = userDoc.getString(HASHED_PASSWORD_KEY);

            // 비밀번호 확인
            if (hashedPasswordFromDB != null && BCrypt.checkpw(password, hashedPasswordFromDB)) {
                return userDoc.getId();
            } else {
                System.err.println("Incorrect password.");
                return null;
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}