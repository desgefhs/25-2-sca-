package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.GameContext;

/**
 * An entity representing a shot fired by the player's ship
 * 
 * @author Kevin Glass
 */
public class ShotEntity extends Entity {
	/** The vertical speed at which the players shot moves */
	private double moveSpeed = -300;
	/** The game context in which this entity exists */
	private GameContext context;
	/** True if this shot has been "used", i.e. its hit something */
	private boolean used = false;
	/** 이 총알이 가하는 데미지 양 */
	private int damage;
	/** 이 총알이 적을 관통하는지 여부 */
	private boolean isPiercing = false;
	
	/**
	 * Create a new shot from the player
	 * 
	 * @param context The game context in which the shot has been created
	 * @param sprite The sprite representing this shot
	 * @param x The initial x location of the shot
	 * @param y The initial y location of the shot
	 * @param damage 이 총알이 가하는 데미지
	 * @param isPiercing 이 총알의 관통 여부
	 */
	public ShotEntity(GameContext context,String sprite,int x,int y, int damage, boolean isPiercing) {
		super(sprite,x,y);
		
		this.context = context;
		this.damage = damage;
		this.isPiercing = isPiercing;
		
		dy = moveSpeed;
	}

	public int getDamage() {
		return damage;
	}

	/**
	 * Request that this shot moved based on time elapsed
	 * 
	 * @param delta The time that has elapsed since last move
	 */
	public void move(long delta) {
		// proceed with normal move
		super.move(delta);


		// if we shot off the screen, remove ourselfs
		if (y < -100) {
			context.removeEntity(this);
		}
	}
	
	/**
	 * Notification that this shot has collided with another
	 * entity
	 * 
	 * @parma other The other entity with which we've collided
	 */
	public void collidedWith(Entity other) {
		// prevents double kills, if we've already hit something,
		// don't collide
		if (used) {
			return;
		}
		
		// if this is a normal shot, it should be removed on impact with anything that has health
		if (!isPiercing && other.getHealth() != null) {
			context.removeEntity(this);
			used = true;
		}
	}
}