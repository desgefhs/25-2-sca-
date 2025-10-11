package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어에게 일시적인 공격력 및 발사 속도 버프를 부여하는 펫 엔티티
 */
public class BuffPetEntity extends PetEntity {

    /** 버프 재사용 대기시간 (6초) */
    private static final long BUFF_COOLDOWN = 6000;
    /** 버프 펫 스프라이트 경로 */
    private static final String BUFF_PET_SPRITE = "sprites/pet/Buffpet.gif";

    /**
     * 새로운 버프 펫을 생성
     *
     * @param game   펫이 존재할 게임 컨텍스트
     * @param player 따라다닐 플레이어 우주선
     * @param x      초기 x 좌표
     * @param y      초기 y 좌표
     */
    public BuffPetEntity(GameContext game, ShipEntity player, int x, int y) {
        super(game, player, BUFF_PET_SPRITE, x, y, BUFF_COOLDOWN);
        setScale(1.0);
    }

    /**
     * 펫의 버프 능력을 활성화
     * 플레이어에게 버프를 부여하고, 버프가 종료될 때 이 펫의 능력 쿨다운을 리셋
     */
    @Override
    public void activateAbility() {
        // ShipEntity의 activateBuff 메서드를 호출하여 버프를 적용
        // 버프 종료 시 이 펫의 쿨다운을 리셋하도록 `resetAbilityCooldown` 메서드를 전달
        GameManager gm = (GameManager) this.game;
        int level = gm.getCurrentPlayer().getPetLevel(PetType.BUFF.name());
        player.activateBuff(level, this::resetAbilityCooldown);
    }
}
