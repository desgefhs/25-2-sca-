package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.PetType;

/**
 * A pet that grants a temporary damage and fire rate buff to the player.
 */
public class BuffPetEntity extends PetEntity {

    private static final long BUFF_COOLDOWN = 6000; // 6 seconds
    // TODO: Replace with actual buff pet sprite
    private static final String BUFF_PET_SPRITE = "sprites/pet/Buffpet.gif";

    /**
     * Create a new buff pet.
     *
     * @param game   The game context in which the pet exists
     * @param player The player ship to follow
     * @param x      The initial x location
     * @param y      The initial y location
     */
    public BuffPetEntity(GameContext game, ShipEntity player, int x, int y) {
        super(game, player, BUFF_PET_SPRITE, x, y, BUFF_COOLDOWN);
        setScale(1.0);
    }

    @Override
    public void activateAbility() {
        // Grant the buff to the player and provide a callback to reset the cooldown when it ends.
        GameManager gm = (GameManager) this.game;
        int level = gm.getCurrentPlayer().getPetLevel(PetType.BUFF.name());
        player.activateBuff(level, this::resetAbilityCooldown);
    }
}
