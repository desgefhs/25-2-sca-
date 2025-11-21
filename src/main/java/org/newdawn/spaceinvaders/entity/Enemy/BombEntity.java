package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.events.AlienKilledEvent;
import org.newdawn.spaceinvaders.core.events.PlayerDiedEvent;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics;

public class BombEntity extends Entity implements Enemy {

    private enum State { APPROACHING, WARNING, EXPLODING }
    private State currentState = State.APPROACHING;

    private final GameContext context;
    private final double moveSpeed = 75; // Halved the speed

    // Warning & Explosion Stats
    private static final long WARNING_DURATION = 1000L; // 1 second
    private static final int WARNING_RANGE = 100; // 1/5 of screen height (600)
    private static final int EXPLOSION_RADIUS = 100;
    private static final int EXPLOSION_DAMAGE = 1;

    private long stateTimer = 0;

    // Warning Animation
    private final Sprite[] warningFrames = new Sprite[13];
    private int currentWarningFrame = 0;
    private static final long WARNING_FRAME_DURATION = WARNING_DURATION / 13;

    public BombEntity(GameContext context, int x, int y) {
        super("sprites/enemy/bomb.gif", x, y);
        this.context = context;
        this.dy = moveSpeed;

        // Pre-load warning animation frames
        for (int i = 0; i < 13; i++) {
            String frame = (i < 10) ? "0" + i : String.valueOf(i);
            warningFrames[i] = SpriteStore.get().getSprite("sprites/radar/" + frame + ".png");
        }
    }

    @Override
    public void move(long delta) {
        super.move(delta);

        ShipEntity ship = context.getShip();
        if (ship == null) return;

        double distanceToShip = Math.sqrt(Math.pow(ship.getX() - x, 2) + Math.pow(ship.getY() - y, 2));

        switch (currentState) {
            case APPROACHING:
                if (distanceToShip <= WARNING_RANGE) {
                    currentState = State.WARNING;
                    stateTimer = WARNING_DURATION;
                }
                break;

            case WARNING:
                stateTimer -= delta;
                // Update which animation frame to show
                currentWarningFrame = (int) (((WARNING_DURATION - stateTimer) / (float) WARNING_DURATION) * 12);

                if (stateTimer <= 0) {
                    currentState = State.EXPLODING;
                }
                break;

            case EXPLODING:
                // Check distance again in case ship moved
                if (distanceToShip <= EXPLOSION_RADIUS) {
                    if (!ship.getHealth().decreaseHealth(EXPLOSION_DAMAGE)) {
                        context.getEventBus().publish(new PlayerDiedEvent());
                    }
                }
                // Create visual explosion
                // Remove self
                context.getEventBus().publish(new AlienKilledEvent());
                context.removeEntity(this);
                break;
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (currentState == State.WARNING) {
            Sprite frame = warningFrames[currentWarningFrame];
            int diameter = EXPLOSION_RADIUS * 2;
            // Draw the warning sprite scaled to the explosion diameter
            g.drawImage(frame.getImage(), (int) (x + (width/2) - (diameter/2)), (int) (y + (height/2) - (diameter/2)), diameter, diameter, null);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // This entity cannot be destroyed by projectiles
        if (other instanceof ProjectileEntity) {
            return;
        }

        // Does no damage on direct collision with the ship
        if (other instanceof ShipEntity) {
        }
    }

    @Override
    public void upgrade() {
        // This entity cannot be upgraded.
    }
}
