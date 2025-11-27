package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 펫 엔티티 생성을 담당하는 팩토리 클래스.
 * {@link PetType}에 따라 적절한 {@link PetEntity} 구현체를 생성하여 반환합니다.
 */
public class PetFactory {

    /**
     * 지정된 타입의 새로운 펫 엔티티를 생성합니다.
     *
     * @param petType 생성할 펫의 타입
     * @param level 펫의 초기 레벨
     * @param game 게임 컨텍스트
     * @param player 펫이 따라다닐 플레이어 함선
     * @param x 펫의 초기 x 위치
     * @param y 펫의 초기 y 위치
     * @return 생성된 {@link PetEntity} 인스턴스. 알 수 없는 타입의 경우 {@link IllegalArgumentException} 발생.
     * @throws IllegalArgumentException 알 수 없는 펫 타입이 주어졌을 경우
     */
    public static PetEntity createPet(PetType petType, int level, GameContext game, ShipEntity player, int x, int y) {
        switch (petType) {
            case ATTACK:
                return new AttackPetEntity(game, player, x, y, level);
            case DEFENSE:
                return new DefensePetEntity(game, player, x, y, level);
            case HEAL:
                return new HealPetEntity(game, player, x, y, level);
            case BUFF:
                return new BuffPetEntity(game, player, x, y, level);
            default:
                // 프로그래밍 오류를 나타내기 위해 예외를 던지는 것이 종종 더 좋습니다.
                throw new IllegalArgumentException("알 수 없는 펫 타입: " + petType);
        }
    }
}
