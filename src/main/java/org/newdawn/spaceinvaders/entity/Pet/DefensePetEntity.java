package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어에게 임시 보호막을 부여하는 펫입니다.
 */
public class DefensePetEntity extends PetEntity {

    private static final long BASE_COOLDOWN = 5000; // 5초
    private static final long COOLDOWN_REDUCTION_PER_LEVEL = 200; // 레벨당 0.2초
    private static final String DEFENSE_PET_SPRITE = "sprites/pet/Defensepet.gif";

    /**
     * 새로운 방어 펫을 생성합니다.
     *
     * @param game         펫이 존재하는 게임 컨텍스트
     * @param player       따라다닐 플레이어 함선
     * @param x            초기 x 위치
     * @param y            초기 y 위치
     * @param initialLevel 펫의 초기 레벨
     */
    public DefensePetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, DEFENSE_PET_SPRITE, x, y, initialLevel);
        setScale(0.5);
        // 펫이 생성될 때 플레이어에게 초기 보호막을 부여합니다.
        player.setShield(true, this::resetAbilityCooldown);
        this.resetAbilityCooldown();
    }

    @Override
    protected void updateStatsByLevel() {
        long reduction = this.level * COOLDOWN_REDUCTION_PER_LEVEL;
        this.abilityCooldown = Math.max(0, BASE_COOLDOWN - reduction); // 재사용 대기시간이 0 미만으로 내려가지 않도록 합니다.
    }

    @Override
    public void activateAbility() {
        // 플레이어가 이미 보호막을 가지고 있지 않은 경우에만 보호막을 부여합니다.
        if (!player.hasShield()) {
            player.setShield(true, this::resetAbilityCooldown);
        }
    }
}