package org.newdawn.spaceinvaders.entity.Pet;

/**
 * 게임 내에서 사용 가능한 다양한 펫의 타입을 정의하는 열거형.
 * 각 펫 타입은 화면에 표시될 이름을 가집니다.
 */
public enum PetType {
    /** 공격형 펫. */
    ATTACK("공격형 펫"),
    /** 방어형 펫. */
    DEFENSE("방어형 펫"),
    /** 치유형 펫. */
    HEAL("치유형 펫"),
    /** 버프형 펫. */
    BUFF("버프형 펫");

    /** 펫 타입의 표시 이름. */
    private final String displayName;

    /**
     * PetType 생성자.
     * @param displayName 펫의 화면 표시 이름
     */
    PetType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 펫 타입의 표시 이름을 반환합니다.
     * @return 펫 타입의 표시 이름
     */
    public String getDisplayName() {
        return displayName;
    }
}
