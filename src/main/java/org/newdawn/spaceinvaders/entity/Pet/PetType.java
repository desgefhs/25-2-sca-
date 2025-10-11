package org.newdawn.spaceinvaders.entity.Pet;

/**
 * 게임에서 사용 가능한 다양한 펫의 유형을 나타냄
 */
public enum PetType {
    /** 공격 펫 */
    ATTACK("Attack Pet"),
    /** 방어 펫 */
    DEFENSE("Defense Pet"),
    /** 치유 펫 */
    HEAL("Heal Pet"),
    /** 버프 펫 */
    BUFF("Buff Pet");

    /** 화면에 표시될 이름 */
    private final String displayName;

    PetType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 펫의 표시 이름을 반환
     * @return 표시 이름
     */
    public String getDisplayName() {
        return displayName;
    }
}
