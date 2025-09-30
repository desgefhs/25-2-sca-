package org.newdawn.spaceinvaders.entity;
import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.graphics.HpRender;

import java.awt.*;

/**
 * The entity that represents the players ship
 * 
 * @author Kevin Glass
 */
public class ShipEntity extends Entity {
	/** The game in which the ship exists */
	private GameContext context;
	private HpRender hpRender;
	private static final int COLLISION_DAMAGE = 1;

	private boolean invincible = false;
    private long invincibilityTimer = 0;
    private static final long INVINCIBILITY_DURATION = 500; // 0.5 seconds
	
	/**
	 * Create a new entity to represent the players ship
	 *  
	 * @param context The game context in which the ship is being created
	 * @param ref The reference to the sprite to show for the ship
	 * @param x The initial x location of the player's ship
	 * @param y The initial y location of the player's ship
	 * @param maxHealth The maximum health of the ship, based on upgrades.
	 */
	public ShipEntity(GameContext context,String ref,int x,int y, int maxHealth) {
		super(ref,x,y);
		// 우주선은 체력을 가지므로, HealthComponent를 생성하여 초기화한다.
		this.health = new HealthComponent(maxHealth);
		this.context = context;
		this.hpRender = new HpRender(health.getHp());
	}

	public void setMaxHealth(int maxHealth) {
	    this.health = new HealthComponent(maxHealth);
	    this.hpRender = new HpRender(health.getHp());
	}
	
	/**
	 * Request that the ship move itself based on an elapsed ammount of
	 * time
	 * 
	 * @param delta The time that has elapsed since last move (ms)
	 */
	public void move(long delta) {
		// Handle invincibility timer
        if (invincible) {
            invincibilityTimer -= delta;
            if (invincibilityTimer <= 0) {
                invincible = false;
            }
        }

		// apply movement
		super.move(delta);

		// then clamp to the screen boundaries
		if (x < 0) {
			x = 0;
		}
		if (x > Game.GAME_WIDTH - width) {
			x = Game.GAME_WIDTH - width;
		}
		if (y < 0) {
			y = 0;
		}
		if (y > Game.GAME_HEIGHT - height) {
			y = Game.GAME_HEIGHT - height;
		}
	}

	@Override
	public void draw(Graphics g) {
	    boolean shouldDraw = true;
        if (invincible) {
            // Blink effect: toggle visibility based on time
            if ((System.currentTimeMillis() / 100) % 2 == 0) {
                shouldDraw = false;
            }
        }

        if (shouldDraw) {
            super.draw(g);
        }

		hpRender.hpRender((Graphics2D) g, this);
	}

	/**
	 * Notification that the player's ship has collided with something
	 * 
	 * @param other The entity with which the ship has collided
	 */
	public void collidedWith(Entity other) {
	    // if invincible, ignore all collisions
        if (invincible) {
            return;
        }

		// if its an alien, notify the game that the player
		// is dead
		if (other instanceof AlienEntity) {
			context.removeEntity(other);
			if(!health.decreaseHealth(COLLISION_DAMAGE)){
				context.notifyDeath();
			} else {
                invincible = true;
                invincibilityTimer = INVINCIBILITY_DURATION;
            }
		}

		if (other instanceof AlienShotEntity) {
		    if (!health.decreaseHealth(((AlienShotEntity) other).getDamage())) {
		        context.notifyDeath();
		    } else {
                invincible = true;
                invincibilityTimer = INVINCIBILITY_DURATION;
            }
		}

		if (other instanceof MeteorEntity) {
            context.removeEntity(other);
            context.notifyDeath();
        }
	}

	public void reset() {
	    health.reset();
	    invincible = false;
        invincibilityTimer = 0;
	    x = Game.GAME_WIDTH / 2;
	    y = 550;
	}
}