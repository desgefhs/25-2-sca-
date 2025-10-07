package org.newdawn.spaceinvaders.entity;


import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.LaserBeamEntity;

/**
 * An entity which represents one of our space invader aliens.
 * 
 * @author Kevin Glass
 */
public class AlienEntity extends Entity {
    private double moveSpeed = 75;
    private GameContext context;

    private int cycle = 0;
	private static final int MAX_HEALTH = 2;
	private static final int SHOT_DAMAGE = 1;

	private long lastFire = 0;
	private static final long firingInterval = 1000;

    // private final EngineFireEntity fireEffect;
    private boolean isUpgraded = false;


	public AlienEntity(GameContext context, String sprite, int x, int y) {
		super(sprite, x, y);
		this.health = new HealthComponent(MAX_HEALTH);
		this.context = context;
		dx = 0;
		dy = moveSpeed;
	}

	public AlienEntity(GameContext context, int x, int y, int health, int cycle) {
		super("sprites/enemy/alien.gif", x, y);
		this.health = new HealthComponent(health);
		this.context = context;
		dx = 0;
		dy = moveSpeed;

        // this.fireEffect = new EngineFireEntity(context, this);
        // this.context.addEntity(this.fireEffect);
	}

	public AlienEntity(GameContext context, int x, int y, int health) {
		this(context, x, y, health, 0);
	}

	public AlienEntity(GameContext context, int x, int y) {
		this(context, x, y, MAX_HEALTH);
	}

    public void upgrade() {
        this.isUpgraded = true;
    }

	private void tryToFire() {
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}

		lastFire = System.currentTimeMillis();
        
        ProjectileType type;
        int damage = 1;

        if (isUpgraded) {
            type = ProjectileType.FOLLOWING_SHOT; // Upgraded shot
            damage = 1;
            ProjectileEntity shot = new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height);
            context.addEntity(shot);
        }


	}

	public void move(long delta) {
		if (Math.random() < 0.002) {
			tryToFire();
		}

		if (y > 600) {
			context.notifyAlienEscaped(this);
		}

		super.move(delta);
	}

    @Override
    public void onDestroy() {
        // if (fireEffect != null) {
        //     context.removeEntity(fireEffect);
        // }
    }

    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    if (!health.decreaseHealth(shot.getDamage())) {
                        context.removeEntity(this);
                        context.notifyAlienKilled();
                    }
                }
            }
        } else if (other instanceof LaserBeamEntity) {
            LaserBeamEntity laser = (LaserBeamEntity) other;
            if (health.isAlive()) {
                if (!health.decreaseHealth(laser.getDamage())) {
                    AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                    explosion.setScale(0.1);
                    int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                    int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
                    explosion.setX(centeredX);
                    explosion.setY(centeredY);
                    context.addEntity(explosion);

                    context.removeEntity(this);
                    context.removeEntity(laser);
                    context.notifyAlienKilled();
                }
            }
        }
    }
}