package org.newdawn.spaceinvaders.data;

/**
 * Firebase에 저장될 플레이어의 데이터를 담는 클래스.
 * 최고 점수와 재화(크레딧) 정보를 포함합니다.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerData {

    private String username;
    private int highScore = 0;
    private int credit = 0;
    private Map<String, Integer> upgradeLevels = new HashMap<>();

    // Firestore가 데이터를 객체로 변환할 때 기본 생성자가 필요합니다.
    public PlayerData() {}

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

    private Map<String, Integer> petLevels = new HashMap<>();

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

    // Pet-related data: Map<PetTypeString, Count>
    private Map<String, Integer> petInventory = new HashMap<>();

    public Map<String, Integer> getPetInventory() {
        return petInventory;
    }

    public void setPetInventory(Map<String, Integer> petInventory) {
        this.petInventory = petInventory;
    }

    private String equippedPet;

    public String getEquippedPet() {
        return equippedPet;
    }

    public void setEquippedPet(String equippedPet) {
        this.equippedPet = equippedPet;
    }
}
