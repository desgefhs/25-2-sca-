package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * An abstract base class for pet entities.
 * It handles the common logic of following the player.
 */
public abstract class PetEntity extends Entity {

    protected final GameContext game; // Game context for the entity to interact with the game
    protected final ShipEntity player;
    private final int offsetX;

    private long lastAbilityTime = 0;
    protected final long abilityCooldown;

    /**
     * Create a new pet entity.
     *
     * @param game           The game context in which the pet exists
     * @param player         The player ship entity to follow
     * @param ref            The sprite reference for this entity
     * @param x              The initial x location of this entity
     * @param y              The initial y location of this entity
     * @param cooldown       The cooldown for the pet's ability in milliseconds
     */
    public PetEntity(GameContext game, ShipEntity player, String ref, int x, int y, long cooldown) {
        super(ref, x, y);
        this.game = game;
        this.player = player;
        this.offsetX = player.getWidth(); // Position the pet to the right
        this.abilityCooldown = cooldown;
    }

    /**
     * Request that this entity move based on time elapsed.
     *
     * @param delta The time that has passed in milliseconds
     */
    @Override
    public void move(long delta) {
        handleMovement(delta);
        handleAbilityActivation(delta);
    }

    /**
     * Handles the movement logic for the pet, following the player.
     * @param delta The time that has passed in milliseconds
     */
    protected void handleMovement(long delta) {
        // Follow the player with an offset
        this.x = player.getX() + offsetX;
        this.y = player.getY();

        super.move(delta);
    }

    /**
     * Handles the logic for activating the pet's ability based on cooldown.
     * @param delta The time that has passed in milliseconds, currently unused but good for future extensions.
     */
    protected void handleAbilityActivation(long delta) {
        // Activate the pet's ability if the cooldown has passed
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAbilityTime > getAbilityCooldown()) {
            lastAbilityTime = currentTime;
            activateAbility();
        }
    }

    /**
     * Gets the cooldown for the pet's ability. Can be overridden by subclasses for dynamic values.
     * @return The ability cooldown in milliseconds.
     */
    protected long getAbilityCooldown() {
        return this.abilityCooldown;
    }

    /**
     * Notification that this entity collided with another.
     * Pets are intangible by default.
     *
     * @param other The entity with which this entity collided.
     */
    @Override
    public void collidedWith(Entity other) {
        // Pets are intangible and do not collide with other entities for now.
    }

    /**
     * Each pet subclass must implement its own special ability.
     */
    public abstract void activateAbility();

    /**
     * Resets the cooldown timer for the pet's ability.
     */
    public void resetAbilityCooldown() {
        this.lastAbilityTime = System.currentTimeMillis();
    }
}
