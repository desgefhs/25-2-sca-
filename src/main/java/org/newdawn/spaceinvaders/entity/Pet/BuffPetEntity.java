package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어에게 일시적인 공격력 및 연사 속도 버프를 부여하는 펫입니다.
 */
public class BuffPetEntity extends PetEntity {

    private static final long BUFF_COOLDOWN = 6000; // 6초
    private static final String BUFF_PET_SPRITE = "sprites/pet/Buffpet.gif";

    /**
     * 새로운 버프 펫을 생성합니다.
     *
     * @param game         펫이 존재하는 게임 컨텍스트
     * @param player       따라다닐 플레이어 함선
     * @param x            초기 x 위치
     * @param y            초기 y 위치
     * @param initialLevel 펫의 초기 레벨
     */
    public BuffPetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, BUFF_PET_SPRITE, x, y, initialLevel);
        setScale(1.0);
    }

    @Override
    protected void updateStatsByLevel() {
        this.abilityCooldown = BUFF_COOLDOWN; // 나중에 레벨에 따라 조정될 수 있습니다.
    }

    @Override
    public void activateAbility() {
        // 플레이어에게 버프를 부여하고, 버프가 끝나면 재사용 대기시간을 재설정하는 콜백을 제공합니다.
        player.activateBuff(this.level, this::resetAbilityCooldown);
    }
}