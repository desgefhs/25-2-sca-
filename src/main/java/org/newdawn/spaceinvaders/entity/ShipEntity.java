package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.GameContext;

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
	private static final int MAX_HEALTH = 3;
	
	/**
	 * Create a new entity to represent the players ship
	 *  
	 * @param context The game context in which the ship is being created
	 * @param ref The reference to the sprite to show for the ship
	 * @param x The initial x location of the player's ship
	 * @param y The initial y location of the player's ship
	 */
	public ShipEntity(GameContext context,String ref,int x,int y) {
		super(ref,x,y);
		// 우주선은 체력을 가지므로, HealthComponent를 생성하여 초기화한다.
		this.health = new HealthComponent(MAX_HEALTH);
		this.context = context;
		this.hpRender = new HpRender(health.getHp());
	}
	
	/**
	 * Request that the ship move itself based on an elapsed ammount of
	 * time
	 * 
	 * @param delta The time that has elapsed since last move (ms)
	 */
	public void move(long delta) {
		// if we're moving left and have reached the left hand side
		// of the screen, don't move
		if ((dx < 0) && (x < 10)) {
			return;
		}
		// if we're moving right and have reached the right hand side
		// of the screen, don't move
		if ((dx > 0) && (x > 750)) {
			return;
		}
		// if we're moving up and have reached the top of the screen, don't move
		if ((dy < 0) && (y < 0)) {
			return;
		}
		// if we're moving down and have reached the bottom of the screen, don't move
		if ((dy > 0) && (y > 600 - sprite.getHeight())) {
			return;
		}
		
		super.move(delta);
	}

	@Override
	public void draw(Graphics g) {
		super.draw(g);
		hpRender.hpRender((Graphics2D) g, this);
	}

	/**
	 * Notification that the player's ship has collided with something
	 * 
	 * @param other The entity with which the ship has collided
	 */
	public void collidedWith(Entity other) {
		// if its an alien, notify the game that the player
		// is dead
		if (other instanceof AlienEntity) {
			context.removeEntity(other);
			if(!health.decreaseHealth(COLLISION_DAMAGE)){
				context.notifyDeath();
			}
		}
	}
}