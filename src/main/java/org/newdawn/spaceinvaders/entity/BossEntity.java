package org.newdawn.spaceinvaders.entity;


import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.graphics.HpRender;

public class BossEntity extends Entity {
    private double moveSpeed = 50;
    private GameContext context;
    private static final int MAX_HEALTH = 50;
    private static final int SHOT_DAMAGE = 2;
    private long lastFire = 0;
    private long firingInterval = 500; // Fires every 0.5 seconds
    private HpRender hpRender;

    public BossEntity(GameContext context, int x, int y, int health, int cycle) {
        super("sprites/boss_cycle" + cycle + ".gif", x, y); // Example of cycle-based sprite
        this.context = context;
        this.health = new HealthComponent(health);
        this.hpRender = new HpRender(this.health.getHp());
        dx = -moveSpeed;
        setScale(2.0);
    }

    public BossEntity(GameContext context, int x, int y, int health) {
        this(context, x, y, health, 0);
    }

    public BossEntity(GameContext context, int x, int y) {
        this(context, x, y, MAX_HEALTH);
    }

    @Override
    public void draw(java.awt.Graphics g) {
        super.draw(g);
        hpRender.hpRender((java.awt.Graphics2D) g, this);
    }

    public void move(long delta) {
        if ((dx < 0) && (x < 0)) {
            dx = -dx; // Bounce off the left wall
        }
        if ((dx > 0) && (x > Game.GAME_WIDTH - width)) {
            dx = -dx; // Bounce off the right wall
        }

        tryToFire();
        super.move(delta);
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        ProjectileType type = ProjectileType.FOLLOWING_SHOT;
        int damage = 2;

        // Fire a spread of 3 shots
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 20, getY() + 50));
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 50, getY() + 50));
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 80, getY() + 50));
    }

    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    if (!health.decreaseHealth(shot.getDamage())) {
                        context.removeEntity(this);
                        context.notifyAlienKilled(); // Notify for score and wave progression
                    }
                }
            }
        }
    }
}
