package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.PetType;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.player.PlayerStats;

/**
 * A pet that attacks enemies.
 */
public class AttackPetEntity extends PetEntity {

    private static final long ATTACK_COOLDOWN = 2000; // 2 seconds
    private static final String ATTACK_PET_SPRITE = "sprites/pet/Attackpet.gif";

    /**
     * Create a new attack pet.
     *
     * @param game   The game context in which the pet exists
     * @param player The player ship to follow
     * @param x      The initial x location
     * @param y      The initial y location
     */
    public AttackPetEntity(GameContext game, ShipEntity player, int x, int y) {
        super(game, player, ATTACK_PET_SPRITE, x, y, ATTACK_COOLDOWN);
    }

    @Override
    public void activateAbility() {
        GameManager gm = (GameManager) game;
        int level = gm.getCurrentPlayer().getPetLevel(PetType.ATTACK.name());
        int projectileCount = 1 + level; // Level 0 = 1 projectile, Level 1 = 2, etc.
        int damage = 1; // Base damage, can be upgraded later

        ProjectileType type = ProjectileType.PLAYER_SHOT; // Use player's shot sprite
        double moveSpeed = type.moveSpeed;

        for (int i=0; i < projectileCount; i++) {
            int xOffset = (i - projectileCount / 2) * 15;
            ProjectileEntity shot = new ProjectileEntity(game, type, damage, getX() + (getWidth()/2) + xOffset, getY() - 30, 0, -moveSpeed);
            game.addEntity(shot);
        }
    }
}
