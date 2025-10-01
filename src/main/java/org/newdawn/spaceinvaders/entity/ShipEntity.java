package org.newdawn.spaceinvaders.entity;
import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.graphics.HpRender;
import org.newdawn.spaceinvaders.player.PlayerStats;

import java.awt.*;

public class ShipEntity extends Entity {
	private GameContext context;
	private HpRender hpRender;
	private static final int COLLISION_DAMAGE = 1;

	private boolean invincible = false;
    private long invincibilityTimer = 0;
    private static final long INVINCIBILITY_DURATION = 500; // 0.5 seconds

    private long lastFire = 0;
	
	public ShipEntity(GameContext context,String ref,int x,int y, int maxHealth) {
		super(ref,x,y);
		this.health = new HealthComponent(maxHealth);
		this.context = context;
		this.hpRender = new HpRender(health.getHp());
	}

	public void setMaxHealth(int maxHealth) {
	    this.health = new HealthComponent(maxHealth);
	    this.hpRender = new HpRender(health.getHp());
	}
	
	public void move(long delta) {
        if (invincible) {
            invincibilityTimer -= delta;
            if (invincibilityTimer <= 0) {
                invincible = false;
            }
        }

		super.move(delta);

		if (x < 0) { x = 0; }
		if (x > Game.GAME_WIDTH - width) { x = Game.GAME_WIDTH - width; }
		if (y < 0) { y = 0; }
		if (y > Game.GAME_HEIGHT - height) { y = Game.GAME_HEIGHT - height; }
	}

    public void tryToFire() {
        GameManager gm = (GameManager) context;
        PlayerStats stats = gm.playerStats;

        if (System.currentTimeMillis() - lastFire < stats.getFiringInterval()) {
            return;
        }
        lastFire = System.currentTimeMillis();

        ProjectileType type = ProjectileType.PLAYER_SHOT;
        int damage = stats.getBulletDamage();
        double moveSpeed = type.moveSpeed;

        for (int i=0; i < stats.getProjectileCount(); i++) {
            int xOffset = (i - stats.getProjectileCount() / 2) * 15;
            ProjectileEntity shot = new ProjectileEntity(context, type, damage, getX() + 10 + xOffset, getY() - 30, 0, -moveSpeed);
			setScale(1);
            context.addEntity(shot);
        }
    }

	@Override
	public void draw(Graphics g) {
	    boolean shouldDraw = true;
        if (invincible) {
            if ((System.currentTimeMillis() / 100) % 2 == 0) {
                shouldDraw = false;
            }
        }

        if (shouldDraw) {
            super.draw(g);
        }

		hpRender.hpRender((Graphics2D) g, this);
	}

	public void collidedWith(Entity other) {
	    if (invincible) {
            return;
        }

		if (other instanceof AlienEntity) {
			context.removeEntity(other);
			if(!health.decreaseHealth(COLLISION_DAMAGE)){
				context.notifyDeath();
			} else {
                invincible = true;
                invincibilityTimer = INVINCIBILITY_DURATION;
            }
		}

		if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.PLAYER) {
                if (!health.decreaseHealth(shot.getDamage())) {
                    context.notifyDeath();
                } else {
                    invincible = true;
                    invincibilityTimer = INVINCIBILITY_DURATION;
                }
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
