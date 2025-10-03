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

    private boolean hasShield = false;
    private Runnable onShieldBreak = null;

    private boolean isBuffActive = false;
    private long buffTimer = 0;
    private static final long BUFF_DURATION = 3000; // 3 seconds
    private int buffLevel = 0;
    private Runnable onBuffEnd = null;

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
	
	                if (isBuffActive) {
	                    buffTimer -= delta;
	                    if (buffTimer <= 0) {
	                        isBuffActive = false;
	                        if (onBuffEnd != null) {
	                            onBuffEnd.run();
	                        }
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

        // Base stats
        long firingInterval = stats.getFiringInterval();
        int bulletDamage = stats.getBulletDamage();
        int projectileCount = stats.getProjectileCount();

        // Apply buff if active
        if (isBuffActive) {
            double buffMultiplier = 1.20 + (buffLevel * 0.01);
            firingInterval /= buffMultiplier; // Faster fire rate
            bulletDamage *= buffMultiplier;   // More damage
        }

        // check that we have waiting long enough to fire
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }

        // if we waited long enough, create the shot(s)
        lastFire = System.currentTimeMillis();
        ProjectileType type = ProjectileType.PLAYER_SHOT;
        double moveSpeed = type.moveSpeed;

        for (int i=0; i < projectileCount; i++) {
            int xOffset = (i - projectileCount / 2) * 15;
            ProjectileEntity shot = new ProjectileEntity(context, type, bulletDamage, getX() + 10 + xOffset, getY() - 30, 0, -moveSpeed);
			setScale(1);
            context.addEntity(shot);
        }
    }

	@Override
	public void draw(Graphics g) {
        int effectSize = Math.max(width, height) + 10;

        // Draw shield visual if active
        if (hasShield) {
            g.setColor(new Color(100, 100, 255, 70)); // Semi-transparent blue
            g.fillOval((int) x - (effectSize - width) / 2, (int) y - (effectSize - height) / 2, effectSize, effectSize);
        }

        // Draw buff visual if active
        if (isBuffActive) {
            g.setColor(new Color(255, 100, 100, 70)); // Semi-transparent red
            g.fillOval((int) x - (effectSize - width) / 2, (int) y - (effectSize - height) / 2, effectSize, effectSize);
        }

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
        // If the colliding entity is a projectile, check if it's hostile before doing anything else.
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType != ProjectileType.TargetType.PLAYER) {
                return; // It's a friendly shot, ignore the collision.
            }
        }

        // Proceed with collision logic only for hostile projectiles, aliens, or meteors.
	    if (other instanceof AlienEntity || other instanceof ProjectileEntity || other instanceof MeteorEntity) {
            // if the shield is active, absorb the hit
            if (hasShield) {
                // Reset cooldown on the provider FIRST, by running the callback.
                if (onShieldBreak != null) {
                    onShieldBreak.run();
                }
                // Now deactivate the shield.
                setShield(false, null);

                // if the colliding entity is not a meteor, remove it
                if (!(other instanceof MeteorEntity)) {
                    context.removeEntity(other);
                }
                return; // Damage absorbed
            }

            // if invincible, no damage
            if (invincible) {
                return;
            }

            // otherwise, take damage
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
                // We already checked the targetType, so we know it's hostile.
                if (!health.decreaseHealth(((ProjectileEntity) other).getDamage())) {
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
	}

	            public void reset() {
	        	    health.reset();
	        	    invincible = false;
	                invincibilityTimer = 0;
	                setShield(false, null); // Also reset shield on reset
	                isBuffActive = false; // Also reset buff
	                buffTimer = 0;
	                onBuffEnd = null;
	        	    x = Game.GAME_WIDTH / 2;
	        	    y = 550;
	        	}
	        
	        	public boolean hasShield() {
	                return hasShield;
	            }
	        
	            public void setShield(boolean hasShield, Runnable onBreak) {
	                this.hasShield = hasShield;
	                this.onShieldBreak = onBreak;
	            }
	        
	            public void activateBuff(int level, Runnable onEnd) {
	                this.isBuffActive = true;
	                this.buffTimer = BUFF_DURATION;
	                this.buffLevel = level;
	                this.onBuffEnd = onEnd;
	            }}
