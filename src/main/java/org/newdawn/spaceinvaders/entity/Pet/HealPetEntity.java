package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.HealthComponent;
import org.newdawn.spaceinvaders.entity.HP;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어를 주기적으로 치유하는 펫 엔티티.
 * {@link PetEntity}를 상속받으며, 레벨에 따라 치유량이 증가합니다.
 */
public class HealPetEntity extends PetEntity {

    /** 펫 능력의 기본 재사용 대기시간 (밀리초). */
    private static final long BASE_COOLDOWN = 5000; // 5초
    /** 치유 펫의 스프라이트 리소스 경로. */
    private static final String HEAL_PET_SPRITE = "sprites/pet/Healpet.gif";

    /** 현재 레벨에 따른 치유량 배율. */
    private double healMultiplier;

    /**
     * HealPetEntity 생성자.
     * @param game 게임 컨텍스트
     * @param player 펫이 따라다닐 플레이어 함선
     * @param x 초기 x 위치
     * @param y 초기 y 위치
     * @param initialLevel 펫의 초기 레벨
     */
    public HealPetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, HEAL_PET_SPRITE, x, y, initialLevel);
        setScale(1.0);
    }

    /**
     * 펫의 현재 레벨에 따라 능력치(치유량 배율)를 업데이트합니다.
     */
    @Override
    protected void updateStatsByLevel() {
        this.abilityCooldown = BASE_COOLDOWN;
        this.healMultiplier = 0.30 + (this.level * 0.02); // 레벨당 치유량 증가
    }

    /**
     * 펫의 능력을 활성화합니다.
     * 플레이어 함선의 체력을 일정 비율만큼 회복시킵니다.
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

        // 플레이어의 체력이 최대가 아닐 때만 치유합니다.
        if (currentHealth < maxHealth) {
            double healAmount = currentHealth * this.healMultiplier;
            double newHealth = Math.min(currentHealth + healAmount, maxHealth);
            hp.setCurrentHp(newHealth);
        }
    }
}