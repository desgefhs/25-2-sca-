package org.newdawn.spaceinvaders.entity;


import org.newdawn.spaceinvaders.core.GameContext;

/**
 * An entity which represents one of our space invader aliens.
 * 
 * @author Kevin Glass
 */
public class AlienEntity extends Entity {
	/**
	 * The speed at which the alient moves horizontally
	 */
	private double moveSpeed = 100;
	/**
	 * The game context in which the entity exists
	 */
	private GameContext context;
	private static final int MAX_HEALTH = 2;
	private static final int SHOT_DAMAGE = 1;

	private static long lastFire = 0;
	private static final long firingInterval = 1000; // 1 second cooldown for the entire fleet

    private final EngineFireEntity fireEffect;


	public AlienEntity(GameContext context, int x, int y, int health, int cycle) {
		super("sprites/alien.gif", x, y);
		this.health = new HealthComponent(health);
		this.context = context;
		dx = 0;
		dy = moveSpeed;

        this.fireEffect = new EngineFireEntity(context, this);
        this.context.addEntity(this.fireEffect);
	}

	public AlienEntity(GameContext context, int x, int y, int health) {
		this(context, x, y, health, 0);
	}

	/**
	 * Create a new alien entity
	 *
	 * @param context The game context in which this entity is being created
	 * @param x       The intial x location of this alien
	 * @param y       The intial y location of this alient
	 */
	public AlienEntity(GameContext context, int x, int y) {
		this(context, x, y, MAX_HEALTH);
	}

	private void tryToFire() {
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}

		// if (Math.random() < 0.01) { // Add randomness if desired
			lastFire = System.currentTimeMillis();
			AlienShotEntity shot = new AlienShotEntity(context, "sprites/alien_shot.gif", getX() + 10, getY() + 30, SHOT_DAMAGE);
			context.addEntity(shot);
		// }
	}

	/**
	 * Request that this alien moved based on time elapsed
	 *
	 * @param delta The time that has elapsed since last move
	 */
	public void move(long delta) {
		// Randomly decide to fire
		if (Math.random() < 0.002) {
			tryToFire();
		}

		// if we have gone off the bottom of the screen, remove ourselfs
		if (y > 600) {
            context.removeEntity(this.fireEffect);
			context.notifyAlienEscaped(this);
		}

		// proceed with normal move
		super.move(delta);
	}

	/**
	 * Notification that this alien has collided with another entity
	 *
	 * @param other The other entity
	 */
	public void collidedWith(Entity other) {
		// 우주선과 충돌한 경우 아무것도 하지 않는다 (우주선 쪽에서만 충돌을 처리).
		if (other instanceof ShipEntity) {
			return;
		}

		// 총알과 충돌한 경우
		if (other instanceof ShotEntity) {
			// 이미 체력이 0 이하라면 아무것도 하지 않음
			if (!health.isAlive()) {
				return;
			}

			// 총알의 데미지만큼 체력을 감소시키고, 체력이 0 이하가 되면 사망 처리
			            if (!health.decreaseHealth(((ShotEntity) other).getDamage())) {
							// Create a scaled, centered explosion
			                AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
			                explosion.setScale(0.1);
			                int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
			                int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
			                explosion.setX(centeredX);
			                explosion.setY(centeredY);
			                context.addEntity(explosion);
			
                            // Remove the fire effect as well
                            context.removeEntity(this.fireEffect);
							// 외계인 자신을 게임에서 제거
							context.removeEntity(this);
							// 외계인이 죽었음을 게임에 알림 (점수 증가, 카운트 감소 등)
							context.notifyAlienKilled();
						}		}
	}
}
