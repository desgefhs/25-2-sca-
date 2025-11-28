package org.newdawn.spaceinvaders.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Firebase Firestore에 저장될 플레이어의 영구 데이터를 담는 POJO(Plain Old Java Object) 클래스.
 * 최고 점수, 재화, 업그레이드, 펫, 무기 등 플레이어의 모든 저장 가능한 정보를 포함합니다.
 */
public class PlayerData {

    /** 플레이어의 사용자 이름. */
    private String username;
    /** 플레이어의 개인 최고 점수. */
    private int highScore = 0;
    /** 플레이어가 소유한 재화(크레딧). */
    private int credit = 0;
    /** 각 업그레이드의 레벨 정보. (Key: 업그레이드 ID, Value: 레벨) */
    private Map<String, Integer> upgradeLevels = new HashMap<>();
    /** 각 펫의 레벨 정보. (Key: 펫 타입, Value: 레벨) */
    private Map<String, Integer> petLevels = new HashMap<>();
    /** 소유한 펫의 인벤토리 정보. (Key: 펫 타입, Value: 개수) */
    private Map<String, Integer> petInventory = new HashMap<>();
    /** 현재 장착 중인 펫의 타입. */
    private String equippedPet;
    /** 현재 장착 중인 무기의 타입. */
    private String equippedWeapon;
    /** 소유한 무기들의 레벨 정보. (Key: 무기 ID, Value: 레벨) */
    private Map<String, Integer> weaponLevels = new HashMap<>();

    /**
     * 기본 생성자.
     * Firestore가 데이터를 객체로 변환할 때 필요합니다.
     */
    public PlayerData() {
        // Firestore 역직렬화를 위해 필요
    }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public Map<String, Integer> getUpgradeLevels() {
        return upgradeLevels;
    }

    public void setUpgradeLevels(Map<String, Integer> upgradeLevels) {
        this.upgradeLevels = upgradeLevels;
    }

    /**
     * 특정 업그레이드의 레벨을 반환합니다.
     * @param upgradeId 조회할 업그레이드의 ID
     * @return 해당 업그레이드의 레벨. 없으면 0.
     */
    public int getUpgradeLevel(String upgradeId) {
        return upgradeLevels.getOrDefault(upgradeId, 0);
    }

    /**
     * 특정 업그레이드의 레벨을 설정합니다.
     * @param upgradeId 레벨을 설정할 업그레이드의 ID
     * @param level 설정할 레벨
     */
    public void setUpgradeLevel(String upgradeId, int level) {
        upgradeLevels.put(upgradeId, level);
    }

    public Map<String, Integer> getPetLevels() {
        return petLevels;
    }

    public void setPetLevels(Map<String, Integer> petLevels) {
        this.petLevels = petLevels;
    }

    /**
     * 특정 펫의 레벨을 반환합니다.
     * @param petType 조회할 펫의 타입
     * @return 해당 펫의 레벨. 없으면 0.
     */
    public int getPetLevel(String petType) {
        return petLevels.getOrDefault(petType, 0);
    }

    /**
     * 특정 펫의 레벨을 1 증가시킵니다.
     * @param petType 레벨을 증가시킬 펫의 타입
     */
    public void increasePetLevel(String petType) {
        petLevels.put(petType, getPetLevel(petType) + 1);
    }

    public Map<String, Integer> getPetInventory() {
        return petInventory;
    }

    public void setPetInventory(Map<String, Integer> petInventory) {
        this.petInventory = petInventory;
    }

    public String getEquippedPet() {
        return equippedPet;
    }

    public void setEquippedPet(String equippedPet) {
        this.equippedPet = equippedPet;
    }

    public String getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(String equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public Map<String, Integer> getWeaponLevels() {
        return weaponLevels;
    }

    public void setWeaponLevels(Map<String, Integer> weaponLevels) {
        this.weaponLevels = weaponLevels;
    }
}