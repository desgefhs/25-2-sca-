package org.newdawn.spaceinvaders.entity;


import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.graphics.HpRender;

public class BossEntity extends Entity {
    private double moveSpeed = 50;
    private double dy = 50; // Add vertical movement speed
    private GameContext context;
    private static final int MAX_HEALTH = 50;
    private static final int SHOT_DAMAGE = 2;
    private long lastFire = 0;
    private long firingInterval = 2000; // Fires every 2 seconds
    private HpRender hpRender;
    private int waveNumber;
    private enum Boss5State { ATTACKING, SPAWNING_ITEMS, CHARGING_LASER }
    private Boss5State boss5State = Boss5State.ATTACKING;
    private long stateTimer = 0;
    private boolean boss10PatternToggle = false; // To alternate patterns for wave 10

    public BossEntity(GameContext context, int x, int y, int health, int cycle, int waveNumber) {
        super(waveNumber == 10 ? "sprites/bosses/Hydra.png" : (waveNumber == 5 ? "sprites/bosses/kraken_anim.gif" : "sprites/boss_cycle" + cycle + ".gif"), x, y);
        this.context = context;
        this.health = new HealthComponent(this,health);
        this.hpRender = new HpRender(this.health.getHp());
        this.waveNumber = waveNumber;
        dx = -moveSpeed;
        if (waveNumber == 10) { // Enable vertical movement for wave 10 boss
            dy = moveSpeed;
        } else {
            dy = 0;
        }
        setScale(2.5);
        if (waveNumber == 5) {
            stateTimer = System.currentTimeMillis();
            context.resetItemCollection();
        }
    }

    public BossEntity(GameContext context, int x, int y, int health, int cycle) {
        this(context, x, y, health, cycle, 0);
    }

    public BossEntity(GameContext context, int x, int y, int health) {
        this(context, x, y, health, 0, 0);
    }

    public BossEntity(GameContext context, int x, int y) {
        this(context, x, y, MAX_HEALTH, 0, 0);
    }

    @Override
    public void draw(java.awt.Graphics g) {
        super.draw(g);
        hpRender.hpRender((java.awt.Graphics2D) g, this);
    }

    public void move(long delta) {
        // Horizontal bouncing
        if ((dx < 0) && (x < 0)) {
            dx = -dx;
        }
        if ((dx > 0) && (x > Game.GAME_WIDTH - width)) {
            dx = -dx;
        }

        // Vertical bouncing for wave 10 boss
        if (waveNumber == 10) {
            if ((dy < 0) && (y < 0)) {
                dy = -dy;
            }
            // Prevent boss from moving too far down
            if ((dy > 0) && (y > 250)) {
                dy = -dy;
            }
        }

        tryToFire();
        super.move(delta);
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        java.util.Random rand = new java.util.Random();

        switch (waveNumber) {
            case 5:
                long timeSinceStateChange = System.currentTimeMillis() - stateTimer;
                switch (boss5State) {
                    case ATTACKING:
                        // Randomly choose between two patterns during normal attack phase
                        if (rand.nextBoolean()) {
                            fireCirclePattern();
                        }
                        else {
                            fireThreeWayPattern();
                        }
                        if (timeSinceStateChange > 5000) { // Attack for 5 seconds
                            boss5State = Boss5State.SPAWNING_ITEMS;
                            stateTimer = System.currentTimeMillis();
                        }
                        break;
                    case SPAWNING_ITEMS:
                        context.resetItemCollection(); // Reset counter before spawning new items
                        context.addEntity(new ItemEntity(context, Game.GAME_WIDTH / 4, 100));
                        context.addEntity(new ItemEntity(context, Game.GAME_WIDTH * 3 / 4, 100));
                        boss5State = Boss5State.CHARGING_LASER;
                        stateTimer = System.currentTimeMillis();
                        break;
                    case CHARGING_LASER:
                        if (timeSinceStateChange > 3000) { // Wait 3 seconds after spawning items
                            int laserWidth = 30; // Assuming this is the original image width
                            for (int x = 0; x < Game.GAME_WIDTH; x += laserWidth) {
                                context.addEntity(new LaserEntity(context, x, laserWidth));
                            }

                            // Reset for the next cycle
                            boss5State = Boss5State.ATTACKING;
                            stateTimer = System.currentTimeMillis() + 4000; // Add laser duration to delay next attack
                        }
                        break;
                }
                break;
            case 10:
                // Wave 10 boss alternates between following shot and curtain pattern
                if (boss10PatternToggle) {
                    fireFollowingShotPattern();
                } else {
                    fireCurtainPattern();
                }
                boss10PatternToggle = !boss10PatternToggle; // Flip the toggle for the next shot
                break;
            case 15:
                // Wave 15 boss randomly uses following shot or circle pattern
                if (rand.nextBoolean()) {
                    fireFollowingShotPattern();
                } else {
                    fireCirclePattern();
                }
                break;
            case 20:
                // Wave 20 boss randomly uses curtain or three-way pattern
                if (rand.nextBoolean()) {
                    fireCurtainPattern();
                } else {
                    fireThreeWayPattern();
                }
                break;
            case 25:
                // Final boss uses one of four patterns randomly
                int pattern = rand.nextInt(4);
                switch (pattern) {
                    case 0: fireThreeWayPattern(); break;
                    case 1: fireCirclePattern(); break;
                    case 2: fireFollowingShotPattern(); break;
                    case 3: fireCurtainPattern(); break;
                }
                break;
            default:
                // Default pattern for other waves if any
                fireFollowingShotPattern();
                break;
        }
    }

    private void fireCurtainPattern() {
        ProjectileType type = ProjectileType.HYDRA_CURTAIN;
        int damage = 1;
        double shotMoveSpeed = type.moveSpeed;
        int gapWidth = 100; // Width of the safe zone
        int gapPosition = new java.util.Random().nextInt(Game.GAME_WIDTH - gapWidth);
        int projectileWidth = 10; // Approximate width of the projectile sprite

        for (int x = 0; x < Game.GAME_WIDTH; x += projectileWidth) {
            if (x > gapPosition && x < gapPosition + gapWidth) {
                continue; // Skip creating a projectile in the gap
            }
            context.addEntity(new ProjectileEntity(context, type, damage, x, 0, 0, shotMoveSpeed));
        }
    }

    private void fireThreeWayPattern() {
        ProjectileType type = ProjectileType.NORMAL_SHOT;
        int damage = 1;
        double shotMoveSpeed = type.moveSpeed;
        double angle = Math.toRadians(30);

        // Center shot (0 degrees)
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, 0, shotMoveSpeed));

        // Left shot (-30 degrees)
        double dxLeft = -Math.sin(angle) * shotMoveSpeed;
        double dyLeft = Math.cos(angle) * shotMoveSpeed;
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, dxLeft, dyLeft));

        // Right shot (+30 degrees)
        double dxRight = Math.sin(angle) * shotMoveSpeed;
        double dyRight = Math.cos(angle) * shotMoveSpeed;
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, dxRight, dyRight));
    }

    private void fireCirclePattern() {
        ProjectileType type = ProjectileType.NORMAL_SHOT;
        int damage = 1;
        int numShots = 12; // Number of shots in the circle
        double shotMoveSpeed = type.moveSpeed;

        for (int i = 0; i < numShots; i++) {
            double angle = Math.toRadians(360.0 / numShots * i);
            double dx = Math.sin(angle) * shotMoveSpeed;
            double dy = Math.cos(angle) * shotMoveSpeed;
            context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width / 2), getY() + (height / 2), dx, dy));
        }
    }

    private void fireFollowingShotPattern() {
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
