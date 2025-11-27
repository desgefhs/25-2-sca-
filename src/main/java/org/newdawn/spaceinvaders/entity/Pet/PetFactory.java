package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * A factory for creating pet entities.
 */
public class PetFactory {

    /**
     * Creates a new pet entity based on the specified type.
     *
     * @param petType      The type of pet to create
     * @param level        The initial level of the pet
     * @param game         The game context
     * @param player       The player ship to follow
     * @param x            The initial x location
     * @param y            The initial y location
     * @return The created PetEntity, or null if the type is unknown.
     */
    public static PetEntity createPet(PetType petType, int level, GameContext game, ShipEntity player, int x, int y) {
        switch (petType) {
            case ATTACK:
                return new AttackPetEntity(game, player, x, y, level);
            case DEFENSE:
                return new DefensePetEntity(game, player, x, y, level);
            case HEAL:
                return new HealPetEntity(game, player, x, y, level);
            case BUFF:
                return new BuffPetEntity(game, player, x, y, level);
            default:
                // Throwing an exception is often better to indicate a programming error
                throw new IllegalArgumentException("Unknown pet type: " + petType);
        }
    }
}
