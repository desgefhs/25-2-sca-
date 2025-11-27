package org.newdawn.spaceinvaders.player;

/**
 * 플레이어의 펫(Pet)에 대한 스탯과 업그레이드 레벨을 저장하는 데이터 클래스.
 */
public class PetStats {

    /** 공격형 펫의 현재 레벨. */
    private int attackPetLevel = 0;

    /**
     * 공격형 펫의 레벨을 반환합니다.
     * @return 공격형 펫의 레벨
     */
    public int getAttackPetLevel() {
        return attackPetLevel;
    }

    /**
     * 공격형 펫의 레벨을 설정합니다.
     * @param attackPetLevel 설정할 레벨 값
     */
    public void setAttackPetLevel(int attackPetLevel) {
        this.attackPetLevel = attackPetLevel;
    }

    /**
     * 공격형 펫의 레벨을 1 증가시킵니다.
     */
    public void increaseAttackPetLevel() {
        this.attackPetLevel++;
    }

    /** 방어형 펫의 현재 레벨. */
    private int defensePetLevel = 0;

    /**
     * 방어형 펫의 레벨을 반환합니다.
     * @return 방어형 펫의 레벨
     */
    public int getDefensePetLevel() {
        return defensePetLevel;
    }

    /**
     * 방어형 펫의 레벨을 1 증가시킵니다.
     */
    public void increaseDefensePetLevel() {
        this.defensePetLevel++;
    }
}
