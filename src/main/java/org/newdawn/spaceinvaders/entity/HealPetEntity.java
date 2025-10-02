package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.PetType;

/**
 * A pet that heals the player.
 */
public class HealPetEntity extends PetEntity {

    private static final long HEAL_COOLDOWN = 5000; // 5 seconds
    private static final String HEAL_PET_SPRITE = "sprites/pet/Healpet.gif";

    /**
     * Create a new heal pet.
     *
     * @param game   The game context in which the pet exists
     * @param player The player ship to follow
     * @param x      The initial x location
     * @param y      The initial y location
     */
    public HealPetEntity(GameContext game, ShipEntity player, int x, int y) {
        super(game, player, HEAL_PET_SPRITE, x, y, HEAL_COOLDOWN);
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
            GameManager gm = (GameManager) this.game;
            int level = gm.getCurrentPlayer().getPetLevel(PetType.HEAL.name());
            double healAmount = currentHealth * (0.30 + (level * 0.02));
            double newHealth = Math.min(currentHealth + healAmount, maxHealth);
            hp.setCurrentHp(newHealth);
        }
    }
}
