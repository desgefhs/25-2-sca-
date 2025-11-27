package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어에게 임시 보호막을 부여하여 피해를 막아주는 펫 엔티티.
 * {@link PetEntity}를 상속받으며, 레벨에 따라 보호막 재사용 대기시간이 감소합니다.
 */
public class DefensePetEntity extends PetEntity {

    /** 펫 능력의 기본 재사용 대기시간 (밀리초). */
    private static final long BASE_COOLDOWN = 5000; // 5초
    /** 레벨당 감소하는 재사용 대기시간 (밀리초). */
    private static final long COOLDOWN_REDUCTION_PER_LEVEL = 200; // 레벨당 0.2초
    /** 방어 펫의 스프라이트 리소스 경로. */
    private static final String DEFENSE_PET_SPRITE = "sprites/pet/Defensepet.gif";

    /**
     * DefensePetEntity 생성자.
     * @param game 게임 컨텍스트
     * @param player 펫이 따라다닐 플레이어 함선
     * @param x 초기 x 위치
     * @param y 초기 y 위치
     * @param initialLevel 펫의 초기 레벨
     */
    public DefensePetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, DEFENSE_PET_SPRITE, x, y, initialLevel);
        setScale(0.5);
        // 펫이 생성될 때 플레이어에게 초기 보호막을 부여하고 재사용 대기시간을 재설정합니다.
        player.setShield(true, this::resetAbilityCooldown);
        this.resetAbilityCooldown();
    }

    /**
     * 펫의 현재 레벨에 따라 능력치(재사용 대기시간)를 업데이트합니다.
     */
    @Override
    protected void updateStatsByLevel() {
        long reduction = this.level * COOLDOWN_REDUCTION_PER_LEVEL;
        this.abilityCooldown = Math.max(0, BASE_COOLDOWN - reduction); // 재사용 대기시간이 0 미만으로 내려가지 않도록 합니다.
    }

    /**
     * 펫의 능력을 활성화합니다.
     * 플레이어 함선에 보호막을 부여하고 재사용 대기시간을 재설정합니다.
     */
    @Override
    public void activateAbility() {
        // 플레이어가 이미 보호막을 가지고 있지 않은 경우에만 보호막을 부여합니다.
        if (!player.hasShield()) {
            player.setShield(true, this::resetAbilityCooldown);
        }
    }
}