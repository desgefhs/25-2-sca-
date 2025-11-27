package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.HealthComponent;
import org.newdawn.spaceinvaders.entity.HP;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어를 치유하는 펫입니다.
 */
public class HealPetEntity extends PetEntity {

    private static final long BASE_COOLDOWN = 5000; // 5초
    private static final String HEAL_PET_SPRITE = "sprites/pet/Healpet.gif";

    private double healMultiplier;

    /**
     * 새로운 치유 펫을 생성합니다.
     *
     * @param game         펫이 존재하는 게임 컨텍스트
     * @param player       따라다닐 플레이어 함선
     * @param x            초기 x 위치
     * @param y            초기 y 위치
     * @param initialLevel 펫의 초기 레벨
     */
    public HealPetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, HEAL_PET_SPRITE, x, y, initialLevel);
        setScale(1.0);
    }

    @Override
    protected void updateStatsByLevel() {
        this.abilityCooldown = BASE_COOLDOWN;
        this.healMultiplier = 0.30 + (this.level * 0.02);
    }

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