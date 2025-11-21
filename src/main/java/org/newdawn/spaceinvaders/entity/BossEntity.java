package org.newdawn.spaceinvaders.entity;


import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.Enemy.Enemy;
import org.newdawn.spaceinvaders.entity.Enemy.TentacleAttackEntity;
import org.newdawn.spaceinvaders.entity.Enemy.AlienEntity;
import org.newdawn.spaceinvaders.entity.Enemy.SweepingLaserEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.graphics.HpRender;

public abstract class BossEntity extends Entity implements Enemy {
    @FunctionalInterface
    protected interface BossPattern {
        void execute();
    }

    protected final java.util.List<BossPattern> availablePatterns = new java.util.ArrayList<>();
    protected BossPattern lastUsedPattern = null;

    protected GameContext context;
    private long lastFire = 0;
    private final long firingInterval; // Fires every 2.5 seconds
    private final HpRender hpRender;
    private boolean isFiringFeatherStream = false;
    private int featherStreamCount = 0;
    private long lastFeatherShotTime = 0;
    private boolean isTeleporting = false;
    private final long teleportStartTime = 0;
    private final long teleportDisappearTime = 500; // ms boss is invisible
    private long laserGimmickStartTime = 0;
    private final java.util.Random random;


    public BossEntity(GameContext context, String sprite, int x, int y, int health) {
        super(sprite, x, y);
        final double moveSpeed = 50;
        this.context = context;
        this.health = new HealthComponent(this,health);
        this.hpRender = new HpRender(this.health.getHp());
        this.firingInterval = 2500; // 2.5초 딜레이
        dx = -moveSpeed;
        dy = 0;

        setScale(2.5);
        this.random = new java.util.Random(); // Initialize the random instance

        setupPatterns();
    }

    protected abstract void setupPatterns();

    @Override
    public void draw(java.awt.Graphics g) {
        if (isTeleporting && System.currentTimeMillis() - teleportStartTime < teleportDisappearTime) {
            // Don't draw while invisible
            return;
        }
        super.draw(g);
        hpRender.hpRender((java.awt.Graphics2D) g, this);
    }

    public void move(long delta) {
        final long teleportTotalTime = 1000; // ms until boss reappears and fires
        final int featherStreamSize = 5;
        final long featherShotDelay = 100;

        // Handle special states first, and return to prevent normal movement
        if (isTeleporting) {
            handleTeleportation();
            return; // Boss is invisible and immobile
        }

        if (isFiringFeatherStream) {
            if (System.currentTimeMillis() - lastFeatherShotTime > featherShotDelay) {
                if (featherStreamCount < featherStreamSize) {
                    ProjectileType type = ProjectileType.FEATHER_SHOT;
                    int damage = 1;
                    double shotMoveSpeed = type.moveSpeed;
                    context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width / 2), getY() + (height / 2), 0, shotMoveSpeed));
                    lastFeatherShotTime = System.currentTimeMillis();
                    featherStreamCount++;
                } else {
                    isFiringFeatherStream = false;
                }
            }
        }

        // Standard movement and attacks
        // Horizontal bouncing
        if ((dx < 0) && (x < 0)) {
            dx = -dx;
        }
        if ((dx > 0) && (x > Game.GAME_WIDTH - width)) {
            dx = -dx;
        }

        if (laserGimmickStartTime != 0 && System.currentTimeMillis() - laserGimmickStartTime > 3000) {
            LaserEntity laser = new LaserEntity(context, 0, Game.GAME_WIDTH);
            context.addEntity(laser);
            laserGimmickStartTime = 0;
        }

        tryToFire();
        super.move(delta);
    }

    private void handleTeleportation() {
        final long teleportTotalTime = 1000; // ms until boss reappears and fires
        if (System.currentTimeMillis() - teleportStartTime >= teleportTotalTime) {
            isTeleporting = false;
            int newX = random.nextInt(Game.GAME_WIDTH - getWidth());
            setX(newX);
            fireCirclePattern();
        }
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        java.util.List<BossPattern> patternsToUse = availablePatterns;

        if (patternsToUse == null || patternsToUse.isEmpty()) {
            return;
        }

        // Select a random pattern from the list for the current phase
        java.util.List<BossPattern> selectablePatterns = new java.util.ArrayList<>(patternsToUse);
        if (lastUsedPattern != null && selectablePatterns.size() > 1) {
            selectablePatterns.remove(lastUsedPattern);
        }

        BossPattern selectedPattern = selectablePatterns.get(random.nextInt(selectablePatterns.size()));

        selectedPattern.execute();
        this.lastUsedPattern = selectedPattern;
    }

    protected void fireCurtainPattern() {
        ProjectileType type = ProjectileType.HYDRA_CURTAIN;
        int damage = 1;
        double shotMoveSpeed = type.moveSpeed;
        int gapWidth = 100; // Width of the safe zone
        int gapPosition = random.nextInt(Game.GAME_WIDTH - gapWidth);
        int projectileWidth = 10; // Approximate width of the projectile sprite

        for (int x = 0; x < Game.GAME_WIDTH; x += projectileWidth) {
            if (x > gapPosition && x < gapPosition + gapWidth) {
                continue; // Skip creating a projectile in the gap
            }
            context.addEntity(new ProjectileEntity(context, type, damage, x, 0, 0, shotMoveSpeed));
        }
    }

    protected void fireThreeWayPattern() {
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

    protected void fireCirclePattern() {
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

    protected void fireFollowingShotPattern() {
        ProjectileType type = ProjectileType.FOLLOWING_SHOT;
        int damage = 2;

        // Fire a spread of 3 shots
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 20, getY() + 50));
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 50, getY() + 50));
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 80, getY() + 50));
    }

    protected void fireGlobalLaserPattern() {
        context.resetItemCollection();
        laserGimmickStartTime = System.currentTimeMillis();

        // Spawn two item entities at random x positions
        context.addEntity(new ItemEntity(context, random.nextInt(Game.GAME_WIDTH), 50));
        context.addEntity(new ItemEntity(context, random.nextInt(Game.GAME_WIDTH), 50));
    }

    protected void fireFeatherPattern() {
        ProjectileType type = ProjectileType.FEATHER_SHOT;
        int damage = 1;
        double shotMoveSpeed = type.moveSpeed;
        int numShots = 5; // 5 shots in the fan
        double fanAngle = Math.toRadians(90); // 90-degree fan

        double startAngle = -fanAngle / 2;
        double angleStep = fanAngle / (numShots - 1);

        for (int i = 0; i < numShots; i++) {
            double angle = startAngle + i * angleStep;
            double dx = Math.sin(angle) * shotMoveSpeed;
            double dy = Math.cos(angle) * shotMoveSpeed;
            context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width / 2), getY() + (height / 2), dx, dy));
        }
    }

    protected void fireFeatherStreamPattern() {
        isFiringFeatherStream = true;
        featherStreamCount = 0;
        lastFeatherShotTime = 0;
    }

    protected void fireTentacleAttackPattern() {
        int numberOfAttacks = 6;
        for (int i = 0; i < numberOfAttacks; i++) {
            // Spawn at a random location on the screen.
            // The area should be restricted to the game area.
            int randomX = (int) (random.nextDouble() * (Game.GAME_WIDTH - 100)) + 50; // Avoid edges
            int randomY = (int) (Math.random() * (Game.GAME_HEIGHT - 200)) + 100; // Avoid top/bottom edges
            context.addEntity(new TentacleAttackEntity(context, randomX, randomY));
        }
    }

    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            handleProjectileCollision((ProjectileEntity) other);
        } else if (other instanceof LaserBeamEntity) {
            handleLaserCollision((LaserBeamEntity) other);
        }
    }

    private void handleProjectileCollision(ProjectileEntity shot) {
        if (shot.getType().targetType == ProjectileType.TargetType.ENEMY && health.isAlive()) {
            if (!health.decreaseHealth(shot.getDamage())) {
                this.destroy();
            }
        }
    }

    private void handleLaserCollision(LaserBeamEntity laser) {
        if (health.isAlive()) {
            if (!health.decreaseHealth(laser.getDamage())) {
                this.destroy();
                laser.destroy();
            }
        }
    }
    @Override
    public void upgrade() {
        // This entity cannot be upgraded.
    }
}
