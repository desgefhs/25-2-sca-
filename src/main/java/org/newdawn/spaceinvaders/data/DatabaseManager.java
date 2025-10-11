package org.newdawn.spaceinvaders.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 *  데이터베이스와의 모든 상호작용(데이터 저장, 불러오기, 랭킹 조회)을 처리하는 클래스
 */
public class DatabaseManager {

    private final Firestore db;
    public DatabaseManager(Firestore db) {
        this.db = db;
    }

    /**
     * 전체 PlayerData 객체를 Firestore에 저장하여 기존 데이터를 덮어씁니다.
     *
     * @param uid        업데이트할 사용자의 고유 ID
     * @param playerData 저장할 데이터가 포함된 PlayerData 객체
     */
    public void updatePlayerData(String uid, PlayerData playerData) {
        if (uid == null || uid.trim().isEmpty()) return;
        DocumentReference docRef = db.collection("users").document(uid);

        // PlayerData를 Map으로 변환하여 update() 메소드를 사용
        // 이는 문서 전체를 덮어쓰는 것을 방지
        Map<String, Object> updates = new HashMap<>();
        updates.put("highScore", playerData.getHighScore());
        updates.put("credit", playerData.getCredit());
        updates.put("upgradeLevels", playerData.getUpgradeLevels());
        updates.put("petInventory", playerData.getPetInventory());
        updates.put("petLevels", playerData.getPetLevels()); // 펫 레벨 저장
        updates.put("weaponLevels", playerData.getWeaponLevels()); // 무기 레벨 저장

        ApiFuture<WriteResult> result = docRef.update(updates);
        try {
            System.out.println("PlayerData 업데이트 완료: " + result.get().getUpdateTime());
        } catch (Exception e) {
            System.err.println("PlayerData 업데이트 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * Firestore에서 사용자의 플레이어 데이터를 불러옴
     *
     * @param uid      불러올 사용자의 고유 ID
     * @param username 사용자 이름 (로그 출력용)
     * @return 불러온 PlayerData 객체. 데이터가 없으면 기본값을 가진 새 객체를 반환
     */
    public PlayerData loadPlayerData(String uid, String username) {
        if (uid == null || uid.trim().isEmpty()) return new PlayerData();
        DocumentReference docRef = db.collection("users").document(uid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                PlayerData playerData = document.toObject(PlayerData.class);

                // Firestore의 숫자 타입을 Integer로 안전하게 변환
                if (playerData != null && document.contains("weaponLevels")) {
                    Map<String, Object> rawWeaponLevels = (Map<String, Object>) document.get("weaponLevels");
                    Map<String, Integer> weaponLevels = new HashMap<>();
                    if (rawWeaponLevels != null) {
                        for (Map.Entry<String, Object> entry : rawWeaponLevels.entrySet()) {
                            if (entry.getValue() instanceof Number) {
                                weaponLevels.put(entry.getKey(), ((Number) entry.getValue()).intValue());
                            }
                        }
                    }
                    playerData.setWeaponLevels(weaponLevels);
                }
                System.out.println(username + " 사용자의 데이터 불러오기 성공. 최고 점수: " + playerData.getHighScore() + ", 크레딧: " + playerData.getCredit());
                return playerData;
            } else {
                System.out.println(username + " 사용자의 데이터가 존재하지 않아 새로 생성합니다.");
                return new PlayerData();
            }
        } catch (Exception e) {
            System.err.println("데이터 불러오기 중 오류 발생: " + e.getMessage());
            return new PlayerData();
        }
    }

    /**
     * 데이터베이스에서 상위 10개의 최고 점수 기록을 가져옴
     *
     * @return "이름: 점수" 형식의 문자열 리스트
     */
    public List<String> getHighScores() {
        List<String> highScores = new ArrayList<>();
        if (db == null) return highScores;

        // 'highScore' 필드를 기준으로 내림차순 정렬하여 상위 10개를 가져옴
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