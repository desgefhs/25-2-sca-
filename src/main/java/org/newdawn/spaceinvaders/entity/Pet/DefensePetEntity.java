package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * A pet that grants a temporary shield to the player.
 */
public class DefensePetEntity extends PetEntity {

    private static final long BASE_COOLDOWN = 5000; // 5 seconds
    private static final long COOLDOWN_REDUCTION_PER_LEVEL = 200; // 0.2 seconds
    // TODO: Replace with actual defense pet sprite
    private static final String DEFENSE_PET_SPRITE = "sprites/pet/Defensepet.gif";

    /**
     * Create a new defense pet.
     *
     * @param game   The game context in which the pet exists
     * @param player The player ship to follow
     * @param x      The initial x location
     * @param y      The initial y location
     */
    public DefensePetEntity(GameContext game, ShipEntity player, int x, int y) {
        super(game, player, DEFENSE_PET_SPRITE, x, y, BASE_COOLDOWN);
        setScale(0.5);
    }

    @Override
    protected long getAbilityCooldown() {
        GameManager gm = (GameManager) this.game;
        int level = gm.getCurrentPlayer().getPetLevel(PetType.DEFENSE.name());
        long reduction = level * COOLDOWN_REDUCTION_PER_LEVEL;
        return BASE_COOLDOWN - reduction;
    }

    @Override
    public void activateAbility() {
        // Only grant a shield if the player doesn't already have one.
        if (!player.hasShield()) {
            player.setShield(true, this::resetAbilityCooldown);
        }
    }
}
