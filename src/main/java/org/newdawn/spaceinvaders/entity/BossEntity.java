package org.newdawn.spaceinvaders.entity;


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
        this.width = sprite.getWidth() * 2;
        this.height = sprite.getHeight() * 2;
    }

    public BossEntity(GameContext context, int x, int y, int health) {
        this(context, x, y, health, 0);
    }

    public BossEntity(GameContext context, int x, int y) {
        this(context, x, y, MAX_HEALTH);
    }

    @Override
    public void draw(java.awt.Graphics g) {
        g.drawImage(sprite.getImage(), (int) x, (int) y, this.width, this.height, null);
        hpRender.hpRender((java.awt.Graphics2D) g, this);
    }

    public void move(long delta) {
        if ((dx < 0) && (x < 10)) {
            dx = -dx; // Bounce off the left wall
        }
        if ((dx > 0) && (x > 750)) {
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

        // Fire a spread of 3 shots
        AlienShotEntity shot1 = new AlienShotEntity(context, "sprites/alien_shot.gif", getX() + 20, getY() + 50, SHOT_DAMAGE);
        AlienShotEntity shot2 = new AlienShotEntity(context, "sprites/alien_shot.gif", getX() + 50, getY() + 50, SHOT_DAMAGE);
        AlienShotEntity shot3 = new AlienShotEntity(context, "sprites/alien_shot.gif", getX() + 80, getY() + 50, SHOT_DAMAGE);

        context.addEntity(shot1);
        context.addEntity(shot2);
        context.addEntity(shot3);
    }

    public void collidedWith(Entity other) {
        if (other instanceof ShotEntity) {
            if (!health.isAlive()) {
                return;
            }
            if (!health.decreaseHealth(((ShotEntity) other).getDamage())) {
                context.removeEntity(this);
                context.notifyAlienKilled(); // Notify for score and wave progression
            }
        }
    }
}
