package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * A pet that attacks enemies.
 */
public class AttackPetEntity extends PetEntity {

    private static final long BASE_COOLDOWN = 2000; // 2 seconds
    private static final String ATTACK_PET_SPRITE = "sprites/pet/Attackpet.gif";

    private int projectileCount;

    /**
     * Create a new attack pet.
     *
     * @param game         The game context in which the pet exists
     * @param player       The player ship to follow
     * @param x            The initial x location
     * @param y            The initial y location
     * @param initialLevel The initial level of the pet
     */
    public AttackPetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, ATTACK_PET_SPRITE, x, y, initialLevel);
        setScale(0.07);
    }

    @Override
    protected void updateStatsByLevel() {
        this.abilityCooldown = BASE_COOLDOWN; // Can be adjusted by level later if needed
        this.projectileCount = 1;
        if (level >= 3) {
            this.projectileCount++;
        }
        if (level >= 6) {
            this.projectileCount++;
        }
        if (level >= 10) {
            this.projectileCount++;
        }
    }

    @Override
    public void activateAbility() {
        int damage = 1; // Base damage, can be upgraded later

        ProjectileType type = ProjectileType.PLAYER_SHOT; // Use player's shot sprite
        double moveSpeed = type.moveSpeed;

        for (int i = 0; i < this.projectileCount; i++) {
            int xOffset = (i - this.projectileCount / 2) * 15;
            ProjectileEntity shot = new ProjectileEntity(game, type, damage, getX() + (getWidth() / 2) + xOffset, getY() - 30, 0, -moveSpeed);
            shot.setScale(0.8);
            game.addEntity(shot);
        }
    }
}
