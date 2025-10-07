package org.newdawn.spaceinvaders.entity.Projectile;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

// projectileType에 따라 총알의 모습을 결정
public class ProjectileEntity extends Entity {

    private final GameContext context;
    private final ProjectileType type;
    private final int damage;

    private long homingTimer;
    private boolean used = false;

    // 타입(이미지 경로, 이동속도, 유도시간, 타겟 종류 )
    public ProjectileEntity(GameContext context, ProjectileType type, int damage, int x, int y, double dx, double dy) {
        super(type.spritePath, x, y);
        this.context = context;
        this.type = type;
        this.damage = damage;
        this.dx = dx;
        this.dy = dy;
        this.homingTimer = type.homingDuration;
        setScale(1.5);
    }

    /**
     * Constructor for homing projectiles where initial velocity is not needed.
     */
    public ProjectileEntity(GameContext context, ProjectileType type, int damage, int x, int y) {
        this(context, type, damage, x, y, 0, 0);
    }

    @Override
    public void move(long delta) {
        // Homing logic
        if (homingTimer > 0) {
            homingTimer -= delta;
            ShipEntity ship = context.getShip();
            if (ship != null) {
                double targetX = ship.getX();
                double targetY = ship.getY();
                double diffX = targetX - x;
                double diffY = targetY - y;
                double length = Math.sqrt(diffX * diffX + diffY * diffY);
                if (length > 0) {
                    dx = (diffX / length) * type.moveSpeed;
                    dy = (diffY / length) * type.moveSpeed;
                }
            }
        }

        super.move(delta);

        //스크린 밖으로 나가면 제거
        if (y < -100 || y > Game.GAME_HEIGHT + 100 || x < -100 || x > Game.GAME_WIDTH + 100) {
            context.removeEntity(this);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        if (used) {
            return;
        }

        // Player shots collide with anything that isn't the player and has health
        if (type.targetType == ProjectileType.TargetType.ENEMY) {
            if (!(other instanceof ShipEntity) && other.getHealth() != null) {
                context.removeEntity(this);
                used = true;
            }
        }

        // Enemy shots only collide with the player ship
        if (type.targetType == ProjectileType.TargetType.PLAYER && other instanceof ShipEntity) {
            context.removeEntity(this);
            used = true;
        }
    }

    public int getDamage() {
        return damage;
    }

    public ProjectileType getType() {
        return type;
    }

    @Override
    public void draw(java.awt.Graphics g) {
        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        java.awt.geom.AffineTransform oldTransform = g2d.getTransform();

        try {
            g2d.translate(x + width / 2.0, y + height / 2.0);
            // The original assumed sprites face UP. We assume they face RIGHT.
            if (dx != 0 || dy != 0) {
                double angle = Math.atan2(dy, dx);
                g2d.rotate(angle);
            }
            g2d.drawImage(sprite.getImage(), -width / 2, -height / 2, width, height, null);
        } finally {
            g2d.setTransform(oldTransform);
        }
    }
}
