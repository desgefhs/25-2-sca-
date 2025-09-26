package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.GameContext;

import java.util.List;

import java.awt.Graphics;

public class BombEntity extends Entity {
    private double moveSpeed;
    private GameContext context;
    private HealthComponent health;
    private int wave;

    public BombEntity(GameContext context, String sprite, int x, int y, double moveSpeed, int wave) {
        super(sprite, x, y);
        this.context = context;
        this.moveSpeed = moveSpeed;
        this.health = new HealthComponent(3); // 체력 3으로 설정
        this.wave = wave;
        dy = moveSpeed;
    }

    @Override
    public void draw(Graphics g) {
        int newWidth = (int) (sprite.getWidth() * 1.5);
        int newHeight = (int) (sprite.getHeight() * 1.5);
        g.drawImage(sprite.getImage(), (int) x, (int) y, newWidth, newHeight, null);
    }

    @Override
    public void move(long delta) {
        super.move(delta);
        if (y > 600) {
            context.removeEntity(this);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShotEntity) {
            if (!health.isAlive()) {
                return;
            }
            // 총알 데미지만큼 체력 감소
            if (!health.decreaseHealth(((ShotEntity) other).getDamage())) {
                explode(); // 체력이 0이 되면 폭발
                context.removeEntity(this);
            }
        }
    }

    private void explode() {
        List<Entity> entities = context.getEntities();
        int explosionRadius = 150;
        int explosionDamage = 1 + (this.wave / 5); // 웨이브 기반 데미지 계산

        for (Entity entity : entities) {
            // 자기 자신은 폭발 효과에서 제외
            if (entity == this) {
                continue;
            }

            double distance = Math.sqrt(Math.pow(entity.getX() - this.getX(), 2) + Math.pow(entity.getY() - this.getY(), 2));

            if (distance < explosionRadius) {
                if (entity instanceof AlienEntity) {
                    HealthComponent alienHealth = entity.getHealth();
                    if (alienHealth != null && alienHealth.isAlive()) {
                        if (!alienHealth.decreaseHealth(explosionDamage)) {
                            context.removeEntity(entity);
                            context.notifyAlienKilled();
                        }
                    }
                }
                if (entity instanceof ShipEntity) {
                    context.notifyDeath();
                }
            }
        }
        context.addEntity(new ExplosionEntity(context, "sprites/explosion.gif", this.getX(), this.getY()));
    }

    @Override
    public HealthComponent getHealth() {
        return health;
    }
}
