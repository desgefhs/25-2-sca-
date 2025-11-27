package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * A pet that grants a temporary damage and fire rate buff to the player.
 */
public class BuffPetEntity extends PetEntity {

    private static final long BUFF_COOLDOWN = 6000; // 6 seconds
    private static final String BUFF_PET_SPRITE = "sprites/pet/Buffpet.gif";

    /**
     * Create a new buff pet.
     *
     * @param game         The game context in which the pet exists
     * @param player       The player ship to follow
     * @param x            The initial x location
     * @param y            The initial y location
     * @param initialLevel The initial level of the pet
     */
    public BuffPetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, BUFF_PET_SPRITE, x, y, initialLevel);
        setScale(1.0);
    }

    @Override
    protected void updateStatsByLevel() {
        this.abilityCooldown = BUFF_COOLDOWN; // Can be adjusted by level later if needed
    }

    @Override
    public void activateAbility() {
        // Grant the buff to the player and provide a callback to reset the cooldown when it ends.
        player.activateBuff(this.level, this::resetAbilityCooldown);
    }
}
