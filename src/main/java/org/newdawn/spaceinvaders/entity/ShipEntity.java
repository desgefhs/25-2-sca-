package org.newdawn.spaceinvaders.entity;
import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Enemy.BombEntity;
import org.newdawn.spaceinvaders.entity.Enemy.Enemy;
import org.newdawn.spaceinvaders.entity.Enemy.AlienEntity;
import org.newdawn.spaceinvaders.entity.Enemy.BombEntity;
import org.newdawn.spaceinvaders.entity.Enemy.Enemy;
import org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity;
import org.newdawn.spaceinvaders.entity.Pet.PetEntity;
import org.newdawn.spaceinvaders.entity.Pet.PetType;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.graphics.HpRender;
import org.newdawn.spaceinvaders.player.BuffManager;
import org.newdawn.spaceinvaders.player.BuffType;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

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

    private Weapon currentWeapon;
    private final Map<PetType, PetEntity> activePets = new HashMap<>();

    private BuffManager buffManager;
    private float moveSpeed = 300;

	public ShipEntity(GameContext context,String ref,int x,int y, int maxHealth) {
		super(ref,x,y);
		this.health = new HealthComponent(this, maxHealth);
		this.context = context;
		this.hpRender = new HpRender(health.getHp());
        this.buffManager = new BuffManager(this);
    }

    public void activateBuff(int level, Runnable onEnd) {
        buffManager.addBuff(BuffType.DAMAGE_BOOST);
        onEnd.run();
    }

	public void setMaxHealth(int maxHealth) {
	    this.health = new HealthComponent(this, maxHealth);
	    this.hpRender = new HpRender(health.getHp());
	}
	
	    public void move(long delta) {
            buffManager.update();
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

    public void setWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }

    public void tryToFire() {
        if (!context.canPlayerAttack()) {
            return;
        }
        currentWeapon.fire(context, this);
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
        if (invincible || buffManager.hasBuff(BuffType.INVINCIBILITY)) {
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
        // if invincible, do nothing to the ship or the other entity
        if (isInvincible()) {
            return;
        }

        if (other instanceof Enemy) {
            // Bomb entity has its own explosion logic and no collision damage
            if (other instanceof BombEntity) {
                return;
            }

            // For all other enemies, they are destroyed on collision
            context.removeEntity(other);
            context.notifyAlienKilled(); // This is the critical fix to decrement alienCount

            // And the ship takes collision damage
            if (!health.decreaseHealth(COLLISION_DAMAGE)) {
                context.notifyDeath();
            }
            return; // Collision handled
        }

        if (other instanceof ProjectileEntity) {
            // Projectile damage is handled by HealthComponent, which also grants invincibility
            if (!health.decreaseHealth(((ProjectileEntity) other).getDamage())) {
                context.notifyDeath();
            }
        }
    }
    public void activateInvincibility() {
        invincible = true;
        invincibilityTimer = INVINCIBILITY_DURATION;
    }

    public void reset() {
        health.reset();
        invincible = false;
        invincibilityTimer = 0;
        setShield(false, null);
        buffManager = new BuffManager(this);
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

    public BuffManager getBuffManager() {
        return buffManager;
    }

    public float getMoveSpeed() {
        return this.moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public boolean isInvincible() {
        return invincible || buffManager.hasBuff(BuffType.INVINCIBILITY);
    }
}
