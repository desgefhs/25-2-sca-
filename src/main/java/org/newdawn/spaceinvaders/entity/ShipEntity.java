package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.graphics.HpRender;
import org.newdawn.spaceinvaders.player.BuffManager;
import org.newdawn.spaceinvaders.player.BuffType;
import org.newdawn.spaceinvaders.player.PlayerStats;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

    private Weapon currentWeapon;
    private final Map<PetType, PetEntity> activePets = new HashMap<>();

    private BuffManager buffManager;
    private float moveSpeed = 300; // pixels/sec

    public ShipEntity(GameContext context, String ref, int x, int y, PlayerStats stats, Weapon weapon) {
        super(ref, x, y);
        this.health = new HealthComponent(stats.getMaxHealth());
        this.context = context;
        this.hpRender = new HpRender(health.getHp());
        this.buffManager = new BuffManager(this);
    }

    public void activateBuff(int level, Runnable onEnd) {
        buffManager.addBuff(BuffType.DAMAGE_BOOST);
        onEnd.run();
    }

    public void setMaxHealth(int maxHealth) {
        this.health = new HealthComponent(maxHealth);
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

        super.move(delta);
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

        if (hasShield) {
            g.setColor(new Color(100, 100, 255, 70)); // Semi-transparent blue
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
        if (buffManager.hasBuff(BuffType.INVINCIBILITY)) {
            return;
        }

        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType != ProjectileType.TargetType.PLAYER) {
                return;
            }
        }

        if (other instanceof AlienEntity || other instanceof ProjectileEntity || other instanceof MeteorEntity) {
            if (hasShield) {
                if (onShieldBreak != null) {
                    onShieldBreak.run();
                }
                setShield(false, null);
                if (!(other instanceof MeteorEntity)) {
                    context.removeEntity(other);
                }
                return;
            }

            if (invincible) {
                return;
            }

            if (other instanceof AlienEntity) {
                context.removeEntity(other);
                if (!health.decreaseHealth(COLLISION_DAMAGE)) {
                    context.notifyDeath();
                } else {
                    invincible = true;
                    invincibilityTimer = INVINCIBILITY_DURATION;
                }
            }

            if (other instanceof ProjectileEntity) {
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
}
