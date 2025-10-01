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
		super("sprites/enemy/alien.gif", x, y);
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

		lastFire = System.currentTimeMillis();
        ProjectileType type = ProjectileType.FOLLOWING_SHOT;
        int damage = 1; // Or get from somewhere else
        ProjectileEntity shot = new ProjectileEntity(context, type, damage, getX() + 10, getY() + 30);
        context.addEntity(shot);
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
			context.notifyAlienEscaped(this);
		}

		// proceed with normal move
		super.move(delta);
	}

    @Override
    public void onDestroy() {
        // When this alien is destroyed, also remove its fire effect
        if (fireEffect != null) {
            context.removeEntity(fireEffect);
        }
    }

	/**
	 * Notification that this alien has collided with another entity
	 *
	 * @param other The other entity
	 */
	public void collidedWith(Entity other) {
		// if it's a shot from the player, take damage
		if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    if (!health.decreaseHealth(shot.getDamage())) {
                        // Create a scaled, centered explosion
                        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                        explosion.setScale(0.1);
                        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                        int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
                        explosion.setX(centeredX);
                        explosion.setY(centeredY);
                        context.addEntity(explosion);

                        // Remove self and notify game
                        context.removeEntity(this);
                        context.notifyAlienKilled();
                    }
                }
            }
        }
	}
}