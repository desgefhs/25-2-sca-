package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics;

public class TentacleAttackEntity extends Entity {

    private enum State {
        WARNING,
        ATTACKING
    }

    private State state = State.WARNING;
    private long startTime;
    private final long warningDuration = 1000; // 1 second warning
    private final long attackDuration = 500;   // 0.5 second attack
    private final int damage = 1;

    private Sprite warningSprite;
    private Sprite attackSprite;

    public TentacleAttackEntity(GameContext context, int x, int y) {
        super("sprites/bosses/fireheart_target.png", x, y); // Initial sprite is the warning
        this.context = context;
        this.startTime = System.currentTimeMillis();

        // Pre-load sprites
        this.warningSprite = this.sprite; // from super constructor
        this.attackSprite = SpriteStore.get().getSprite("sprites/bosses/fireheart_tentacle.png");
    }

    @Override
    public void move(long delta) {
        long now = System.currentTimeMillis();
        long timeSinceStart = now - startTime;

        if (state == State.WARNING && timeSinceStart > warningDuration) {
            state = State.ATTACKING;
            this.sprite = attackSprite; // Change sprite
            // Adjust position since tentacle sprite might be different size
            this.x = this.x + (warningSprite.getWidth() / 2) - (attackSprite.getWidth() / 2);
            this.y = this.y + (warningSprite.getHeight() / 2) - (attackSprite.getHeight() / 2);
        } else if (state == State.ATTACKING && timeSinceStart > warningDuration + attackDuration) {
            context.removeEntity(this); // Attack is over
        }
    }

    @Override
    public void collidedWith(Entity other) {
        if (state == State.ATTACKING && other instanceof ShipEntity) {
            ShipEntity ship = (ShipEntity) other;
            ship.getHealth().decreaseHealth(damage);
            // The tentacle attack persists for its duration, so it doesn't get removed on collision.
        }
    }
}