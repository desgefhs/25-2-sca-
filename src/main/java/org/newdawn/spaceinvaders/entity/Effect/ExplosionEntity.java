package org.newdawn.spaceinvaders.entity.Effect;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;

import java.awt.Graphics;

public class ExplosionEntity extends Entity {
    private final long lifeTime = 500; // 0.5초
    private final long createdAt;
    private final GameContext context;

    public ExplosionEntity(GameContext context, String sprite, int x, int y) {
        super(sprite, x, y);
        this.context = context;
        this.createdAt = System.currentTimeMillis();
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
        if (System.currentTimeMillis() - createdAt > lifeTime) {
            context.removeEntity(this);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // 폭발 이펙트는 다른 엔티티와 충돌하지 않음
    }
}
