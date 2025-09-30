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

public class AuthManager {

    private final Firestore db;
    private final CollectionReference usersCollection;

    public AuthManager(Firestore db) {
        this.db = db;
        this.usersCollection = db.collection("users");
    }

    public boolean signUp(String username, String password) {
        try {
            // 존재하는 계정인지 확인
            ApiFuture<QuerySnapshot> future = usersCollection.whereEqualTo("username", username).get();
            QuerySnapshot snapshot = future.get();
            if (!snapshot.isEmpty()) {
                System.err.println("Username already exists.");
                return false;
            }

            // 비밀번호 해쉬값으로 변환
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            System.out.println("DEBUG: Hashed Password to be saved: " + hashedPassword);

            // 새로운 사용자 문서 생성
            Map<String, Object> user = new HashMap<>();
            user.put("username", username);
            user.put("hashedPassword", hashedPassword);

            usersCollection.add(user).get();
            return true;

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String signIn(String username, String password) {
        try {
            // username으로 사용자 찾기
            ApiFuture<QuerySnapshot> future = usersCollection.whereEqualTo("username", username).limit(1).get();
            QuerySnapshot snapshot = future.get();

            if (snapshot.isEmpty()) {
                System.err.println("User not found.");
                return null;
            }

            // 사용자 문서 가져오기
            DocumentSnapshot userDoc = snapshot.getDocuments().get(0);
            String hashedPasswordFromDB = userDoc.getString("hashedPassword");

            // 비밀번호 체크
            if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
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
