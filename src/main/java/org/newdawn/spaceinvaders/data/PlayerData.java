package org.newdawn.spaceinvaders.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Firebase에 저장될 플레이어의 데이터를 클래스
 * 이 클래스의 필드는 Firestore 문서의 필드와 직접 매핑
 */
public class PlayerData {

    private String username;
    private int highScore = 0;
    private int credit = 0;
    /** 업그레이드 레벨 (ID, 레벨) */
    private Map<String, Integer> upgradeLevels = new HashMap<>();
    /** 펫 인벤토리 (펫 타입, 개수) */
    private Map<String, Integer> petInventory = new HashMap<>();
    /** 펫 레벨 (펫 타입, 레벨) */
    private Map<String, Integer> petLevels = new HashMap<>();
    /** 무기 레벨 (무기 이름, 레벨) */
    private Map<String, Integer> weaponLevels = new HashMap<>();
    private String equippedPet;
    private String equippedWeapon;



    public PlayerData() {}


    // --- Getters and Setters ---

    public String getUsername() {
        return username;
    }

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

    public int getUpgradeLevel(String upgradeId) {
        return upgradeLevels.getOrDefault(upgradeId, 0);
    }

    public void setUpgradeLevel(String upgradeId, int level) {
        upgradeLevels.put(upgradeId, level);
    }

    public Map<String, Integer> getPetLevels() {
        return petLevels;
    }

    public void setPetLevels(Map<String, Integer> petLevels) {
        this.petLevels = petLevels;
    }

    public int getPetLevel(String petType) {
        return petLevels.getOrDefault(petType, 0);
    }

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
