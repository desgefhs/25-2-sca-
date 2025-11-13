package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.HealthComponent;
import org.newdawn.spaceinvaders.entity.HP;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * A pet that heals the player.
 */
public class HealPetEntity extends PetEntity {

    private static final long BASE_COOLDOWN = 5000; // 5 seconds
    private static final String HEAL_PET_SPRITE = "sprites/pet/Healpet.gif";

    private double healMultiplier;

    /**
     * Create a new heal pet.
     *
     * @param game         The game context in which the pet exists
     * @param player       The player ship to follow
     * @param x            The initial x location
     * @param y            The initial y location
     * @param initialLevel The initial level of the pet
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

        // Heal only if the player is not at max health
        if (currentHealth < maxHealth) {
            double healAmount = currentHealth * this.healMultiplier;
            double newHealth = Math.min(currentHealth + healAmount, maxHealth);
            hp.setCurrentHp(newHealth);
        }
    }
}
