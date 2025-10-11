package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.*;

/**
 * 주기적으로 플레이어를 치유하는 펫 엔티티
 * 펫의 레벨에 따라 치유량이 증가
 */
public class HealPetEntity extends PetEntity {

    /** 치유 재사용 대기시간 (5초) */
    private static final long HEAL_COOLDOWN = 5000;
    private static final String HEAL_PET_SPRITE = "sprites/pet/Healpet.gif";

    /**
     * 새로운 치유 펫을 생성합
     *
     * @param game   펫이 존재할 게임 컨텍스트
     * @param player 따라다닐 플레이어 우주선
     * @param x      초기 x 좌표
     * @param y      초기 y 좌표
     */
    public HealPetEntity(GameContext game, ShipEntity player, int x, int y) {
        super(game, player, HEAL_PET_SPRITE, x, y, HEAL_COOLDOWN);
        setScale(1.0);
    }

    /**
     * 펫의 치유 능력을 활성화
     * 플레이어의 체력이 최대치가 아닐 경우에만, 현재 체력의 일정 비율만큼 치유
     * 치유 비율은 펫의 레벨에 따라 증가
     */
    @Override
    public void activateAbility() {
        HealthComponent healthComponent = player.getHealth();
        if (healthComponent == null) {
            return;
        }

        HP hp = healthComponent.getHp();
        double currentHealth = hp.getCurrentHp();
        double maxHealth = hp.getMAX_HP();

        // 플레이어의 체력이 최대치가 아닐 때만 치유
        if (currentHealth < maxHealth) {
            GameManager gm = (GameManager) this.game;
            int level = gm.getCurrentPlayer().getPetLevel(PetType.HEAL.name());
            // 기본 30% + 레벨당 2% 추가 치유
            double healAmount = currentHealth * (0.30 + (level * 0.02));
            double newHealth = Math.min(currentHealth + healAmount, maxHealth);
            hp.setCurrentHp(newHealth);
        }
    }
}
