package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어에게 일시적인 공격력 및 연사 속도 버프를 부여하는 펫 엔티티.
 * {@link PetEntity}를 상속받으며, 일정 시간마다 플레이어에게 버프를 제공합니다.
 */
public class BuffPetEntity extends PetEntity {

    /** 버프 능력의 재사용 대기시간 (밀리초). */
    private static final long BUFF_COOLDOWN = 6000; // 6초
    /** 버프 펫의 스프라이트 리소스 경로. */
    private static final String BUFF_PET_SPRITE = "sprites/pet/Buffpet.gif";

    /**
     * BuffPetEntity 생성자.
     * @param game 게임 컨텍스트
     * @param player 펫이 따라다닐 플레이어 함선
     * @param x 초기 x 위치
     * @param y 초기 y 위치
     * @param initialLevel 펫의 초기 레벨
     */
    public BuffPetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, BUFF_PET_SPRITE, x, y, initialLevel);
        setScale(1.0);
    }

    /**
     * 펫의 현재 레벨에 따라 능력치(재사용 대기시간)를 업데이트합니다.
     */
    @Override
    protected void updateStatsByLevel() {
        this.abilityCooldown = BUFF_COOLDOWN; // 기본 재사용 대기시간
        // 레벨에 따라 조정될 수 있음
    }

    /**
     * 펫의 능력을 활성화합니다.
     * 플레이어 함선에 버프 효과를 적용합니다. 버프가 끝나면 재사용 대기시간이 재설정됩니다.
     */
    @Override
    public void activateAbility() {
        // 플레이어에게 버프를 부여하고, 버프가 끝나면 재사용 대기시간을 재설정하는 콜백을 제공합니다.
        player.activateBuff(this.level, this::resetAbilityCooldown);
    }
}