package org.newdawn.spaceinvaders.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Firebase Firestore 데이터베이스와의 모든 상호작용(데이터 저장, 불러오기)을 처리하는 클래스.
 */
public class DatabaseManager {

    private final Firestore db;

    public DatabaseManager(Firestore db) {
        this.db = db;
    }

    /**
     * 특정 사용자의 크레딧과 최고 점수를 업데이트합니다.
     * @param uid 업데이트할 사용자의 고유 ID
     * @param newCredit 새로운 누적 크레딧
     * @param newHighScore 새로운 최고 점수
     */
    public void updatePlayerStats(String uid, int newCredit, int newHighScore) {
        if (uid == null || uid.trim().isEmpty()) return;
        DocumentReference docRef = db.collection("users").document(uid);

        Map<String, Object> updates = new HashMap<>();
        updates.put("credit", newCredit);
        updates.put("highScore", newHighScore);

        ApiFuture<WriteResult> result = docRef.update(updates);
        try {
            System.out.println("점수 및 크레딧 업데이트 완료: " + result.get().getUpdateTime());
        } catch (Exception e) {
            System.err.println("데이터 업데이트 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * Firestore에서 특정 사용자의 플레이어 데이터를 불러옵니다.
     * @param uid 불러올 사용자의 고유 ID
     * @return 불러온 PlayerData 객체. 만약 데이터가 없으면 기본값(highScore=0, credit=0)을 가진 새 객체를 반환합니다.
     */
    public PlayerData loadPlayerData(String uid) {
        if (uid == null || uid.trim().isEmpty()) return new PlayerData();
        DocumentReference docRef = db.collection("users").document(uid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                PlayerData playerData = document.toObject(PlayerData.class);
                System.out.println(uid + " 사용자의 데이터 불러오기 성공. 최고 점수: " + playerData.getHighScore() + ", 크레딧: " + playerData.getCredit());
                return playerData;
            } else {
                System.out.println(uid + " 사용자의 데이터가 존재하지 않아 새로 생성합니다.");
                return new PlayerData();
            }
        } catch (Exception e) {
            System.err.println("데이터 불러오기 중 오류 발생: " + e.getMessage());
            return new PlayerData();
        }
    }

    /**
     * 데이터베이스에서 상위 10개의 최고 점수 기록을 가져옵니다.
     * @return 랭킹 문자열 목록
     */
    public List<String> getHighScores() {
        List<String> highScores = new ArrayList<>();
        if (db == null) return highScores;

        ApiFuture<QuerySnapshot> query = db.collection("users").orderBy("highScore", Query.Direction.DESCENDING).limit(10).get();
        try {
            QuerySnapshot querySnapshot = query.get();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                highScores.add(document.getString("username") + ": " + document.getLong("highScore"));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return highScores;
    }
}