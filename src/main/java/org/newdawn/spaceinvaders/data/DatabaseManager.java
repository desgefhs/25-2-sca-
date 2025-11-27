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

    private static final String USERS_COLLECTION = "users";
    private static final String HIGH_SCORE_FIELD = "highScore";
    private static final String CREDIT_FIELD = "credit";
    private static final String UPGRADE_LEVELS_FIELD = "upgradeLevels";
    private static final String PET_INVENTORY_FIELD = "petInventory";
    private static final String PET_LEVELS_FIELD = "petLevels";
    private static final String WEAPON_LEVELS_FIELD = "weaponLevels";
    private static final String USERNAME_FIELD = "username";

    private static final String LOG_UPDATE_SUCCESS = "PlayerData 업데이트 완료: ";
    private static final String LOG_UPDATE_ERROR = "PlayerData 업데이트 중 오류 발생: ";
    private static final String LOG_LOAD_SUCCESS_PREFIX = " 사용자의 데이터 불러오기 성공. 최고 점수: ";
    private static final String LOG_LOAD_SUCCESS_SUFFIX = ", 크레딧: ";
    private static final String LOG_CREATE_NEW_USER_DATA = " 사용자의 데이터가 존재하지 않아 새로 생성합니다.";
    private static final String LOG_LOAD_ERROR = "데이터 불러오기 중 오류 발생: ";

    private final Firestore db;

    public DatabaseManager(Firestore db) {
        this.db = db;
    }

    /**
     * 전체 PlayerData 객체를 Firestore에 저장하며, 기존 데이터를 덮어씁니다.
     * @param uid 업데이트할 사용자의 고유 ID.
     * @param playerData 저장할 데이터가 포함된 PlayerData 객체.
     */
    public void updatePlayerData(String uid, PlayerData playerData) {
        if (uid == null || uid.trim().isEmpty()) return;
        DocumentReference docRef = db.collection(USERS_COLLECTION).document(uid);

        // PlayerData를 Map으로 변환하여 update() 메소드를 사용합니다.
        // 이렇게 하면 전체 문서를 덮어쓰는 것을 방지할 수 있습니다.
        Map<String, Object> updates = new HashMap<>();
        updates.put(HIGH_SCORE_FIELD, playerData.getHighScore());
        updates.put(CREDIT_FIELD, playerData.getCredit());
        updates.put(UPGRADE_LEVELS_FIELD, playerData.getUpgradeLevels());
        updates.put(PET_INVENTORY_FIELD, playerData.getPetInventory());
        updates.put(PET_LEVELS_FIELD, playerData.getPetLevels()); // 펫 레벨 저장
        updates.put(WEAPON_LEVELS_FIELD, playerData.getWeaponLevels()); // 무기 레벨 저장

        ApiFuture<WriteResult> result = docRef.update(updates);
        try {
            System.out.println(LOG_UPDATE_SUCCESS + result.get().getUpdateTime());
        } catch (Exception e) {
            System.err.println(LOG_UPDATE_ERROR + e.getMessage());
        }
    }

    /**
     * Firestore에서 특정 사용자의 플레이어 데이터를 불러옵니다.
     * @param uid 불러올 사용자의 고유 ID
     * @return 불러온 PlayerData 객체. 만약 데이터가 없으면 기본값(highScore=0, credit=0)을 가진 새 객체를 반환합니다.
     */
    public PlayerData loadPlayerData(String uid, String username) {
        if (uid == null || uid.trim().isEmpty()) return new PlayerData();
        DocumentReference docRef = db.collection(USERS_COLLECTION).document(uid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                PlayerData playerData = document.toObject(PlayerData.class);
                if (playerData != null) {
                    playerData.setWeaponLevels(extractWeaponLevels(document));
                    System.out.println(username + LOG_LOAD_SUCCESS_PREFIX + playerData.getHighScore() + LOG_LOAD_SUCCESS_SUFFIX + playerData.getCredit());
                }
                return playerData;
            } else {
                System.out.println(username + LOG_CREATE_NEW_USER_DATA);
                return new PlayerData();
            }
        } catch (Exception e) {
            System.err.println(LOG_LOAD_ERROR + e.getMessage());
            return new PlayerData();
        }
    }

    private Map<String, Integer> extractWeaponLevels(DocumentSnapshot document) {
        Map<String, Integer> weaponLevels = new HashMap<>();
        if (document.contains(WEAPON_LEVELS_FIELD)) {
            Object rawWeaponLevelsObject = document.get(WEAPON_LEVELS_FIELD);
            if (rawWeaponLevelsObject instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> rawWeaponLevels = (Map<String, Object>) rawWeaponLevelsObject;
                for (Map.Entry<String, Object> entry : rawWeaponLevels.entrySet()) {
                    if (entry.getValue() instanceof Number) {
                        weaponLevels.put(entry.getKey(), ((Number) entry.getValue()).intValue());
                    }
                }
            }
        }
        return weaponLevels;
    }

    /**
     * 데이터베이스에서 상위 10개의 최고 점수 기록을 가져옵니다.
     * @return 랭킹 문자열 목록
     */
    public List<org.newdawn.spaceinvaders.ranking.Ranking> getHighScores() {
        List<org.newdawn.spaceinvaders.ranking.Ranking> highScores = new ArrayList<>();
        if (db == null) return highScores;

        ApiFuture<QuerySnapshot> query = db.collection(USERS_COLLECTION).orderBy(HIGH_SCORE_FIELD, Query.Direction.DESCENDING).limit(10).get();
        try {
            QuerySnapshot querySnapshot = query.get();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                String username = document.getString(USERNAME_FIELD);
                int score = document.getLong(HIGH_SCORE_FIELD).intValue();
                highScores.add(new org.newdawn.spaceinvaders.ranking.Ranking(username, score));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return highScores;
    }
}