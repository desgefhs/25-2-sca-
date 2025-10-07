package org.newdawn.spaceinvaders.entity.Projectile;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;
import org.newdawn.spaceinvaders.entity.Enemy.AlienEntity;
import org.newdawn.spaceinvaders.entity.Enemy.ThreeWayShooter;
import org.newdawn.spaceinvaders.entity.Entity;

import java.awt.Graphics2D;

public class LaserBeamEntity extends Entity {
    private final int duration;
    private final int damage;
    private long startTime;
    private Entity owner;

    public LaserBeamEntity(GameContext context, Entity owner, int duration, int damage) {
        super("sprites/texture_laser.PNG", owner.getX(), owner.getY());
        this.context = context;
        this.owner = owner;
        this.duration = duration;
        this.damage = damage;
        this.startTime = System.currentTimeMillis();
        this.width = 20;
        this.height = 400;
    }

    @Override
    public void move(long delta) {
        if (System.currentTimeMillis() - startTime > duration) {
            context.removeEntity(this);
            return;
        }

        this.x = owner.getX() + owner.getWidth() / 2 - 10;
        this.y = owner.getY() - 400;

        for (Entity other : context.getEntities()) {
            if (other instanceof AlienEntity || other instanceof BossEntity || other instanceof ThreeWayShooter) {
                if (this.collidesWith(other)) {
                    other.collidedWith(this);
                }
            }
        }
    }

    @Override
    public void draw(java.awt.Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(sprite.getImage(), (int)x, (int)y, 20, 400, null);
    }

    @Override
    public void collidedWith(Entity other) {
        // The laser beam itself is not affected by collisions
    }

    public int getDamage() {
        return damage;
    }
}
