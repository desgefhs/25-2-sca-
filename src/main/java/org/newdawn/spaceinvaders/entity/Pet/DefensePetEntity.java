package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어에게 주기적으로 쉴드를 부여하는 펫 엔티티
 * 레벨이 오르면 쉴드 부여의 재사용 대기시간이 감소
 */
public class DefensePetEntity extends PetEntity {

    /** 기본 재사용 대기시간 (5초) */
    private static final long BASE_COOLDOWN = 5000;
    /** 레벨 당 재사용 대기시간 감소량 (0.2초) */
    private static final long COOLDOWN_REDUCTION_PER_LEVEL = 200;
    /** 방어 펫 스프라이트 경로 */
    private static final String DEFENSE_PET_SPRITE = "sprites/pet/Defensepet.gif";

    /**
     * 새로운 방어 펫을 생성
     *
     * @param game   펫이 존재할 게임 컨텍스트
     * @param player 따라다닐 플레이어 우주선
     * @param x      초기 x 좌표
     * @param y      초기 y 좌표
     */
    public DefensePetEntity(GameContext game, ShipEntity player, int x, int y) {
        super(game, player, DEFENSE_PET_SPRITE, x, y, BASE_COOLDOWN);
        setScale(0.5);
    }

    /**
     * 펫의 레벨에 따라 계산된 실제 능력 재사용 대기시간을 반환
     * @return 계산된 재사용 대기시간 (밀리초)
     */
    @Override
    protected long getAbilityCooldown() {
        GameManager gm = (GameManager) this.game;
        int level = gm.getCurrentPlayer().getPetLevel(PetType.DEFENSE.name());
        long reduction = level * COOLDOWN_REDUCTION_PER_LEVEL;
        return Math.max(500, BASE_COOLDOWN - reduction); // 최소 쿨다운 0.5초 보장
    }

    /**
     * 펫의 쉴드 부여 능력을 활성화
     * 플레이어가 이미 쉴드를 가지고 있지 않을 때만 쉴드를 부여
     */
    @Override
    public void activateAbility() {
        if (!player.hasShield()) {
            player.setShield(true, this::resetAbilityCooldown);
        }
    }
}
