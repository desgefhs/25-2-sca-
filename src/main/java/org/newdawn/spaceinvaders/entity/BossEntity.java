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

public class BossEntity extends Entity implements Enemy {
    @FunctionalInterface
    private interface BossPattern {
        void execute();
    }

    private final java.util.List<BossPattern> availablePatterns = new java.util.ArrayList<>();
    private BossPattern lastUsedPattern = null;
    private long lastSpecialAttack = 0;

    private double moveSpeed = 50;
    private double dy = 50; // Add vertical movement speed
    private GameContext context;
    private static final int MAX_HEALTH = 50;
    private static final int SHOT_DAMAGE = 2;
    private long lastFire = 0;
    private long firingInterval = 2500; // Fires every 2.5 seconds
    private HpRender hpRender;
    private int waveNumber;
    private enum Boss5State { ATTACKING, SPAWNING_ITEMS, CHARGING_LASER }
    private Boss5State boss5State = Boss5State.ATTACKING;
    private long stateTimer = 0;
    private boolean boss10PatternToggle = false; // To alternate patterns for wave 10
    private boolean isFiringFeatherStream = false;
    private int featherStreamCount = 0;
    private long lastFeatherShotTime = 0;
    private final int featherStreamSize = 5;
    private final long featherShotDelay = 100;
    private boolean hasSplit = false;
    private boolean isMiniBoss = false;
    private int phase = 1;
    private boolean isTeleporting = false;
    private long teleportStartTime = 0;
    private final long teleportDisappearTime = 500; // ms boss is invisible
    private final long teleportTotalTime = 1000; // ms until boss reappears and fires
    private final java.util.List<LaserEntity> laserGimmicks = new java.util.ArrayList<>();
    private long laserGimmickStartTime = 0;


    public BossEntity(GameContext context, int x, int y, int health, int cycle, int waveNumber, boolean isMiniBoss) {
        super(getBossSprite(waveNumber, cycle), x, y);
        this.context = context;
        this.health = new HealthComponent(this,health);
        this.hpRender = new HpRender(this.health.getHp());
        this.waveNumber = waveNumber;
        this.isMiniBoss = isMiniBoss;
        this.firingInterval = 2500; // 2.5초 딜레이
        dx = -moveSpeed;
        if (waveNumber == 10) { // Enable vertical movement for wave 10 boss
            dy = moveSpeed;
        } else {
            dy = 0;
        }

        if (isMiniBoss) {
            setScale(1.5);
        } else {
            setScale(2.5);
        }

        if (waveNumber == 5) {
            stateTimer = System.currentTimeMillis();
            context.resetItemCollection();
        }
        setupPatterns();
    }

    private void setupPatterns() {
        switch (waveNumber) {
            case 5:
                availablePatterns.add(this::fireCirclePattern);
                availablePatterns.add(this::fireThreeWayPattern);
                availablePatterns.add(this::fireGlobalLaserPattern);
                break;
            case 10:
                availablePatterns.add(this::fireFollowingShotPattern);
                availablePatterns.add(this::fireCurtainPattern);
                break;
            case 15:
                availablePatterns.add(this::fireFeatherPattern);
                availablePatterns.add(this::fireFeatherStreamPattern);
                break;
            case 20:
                availablePatterns.add(this::fireTentacleAttackPattern);
                break;
            case 25:
                // Patterns are now handled dynamically in tryToFire based on phase
                break;
            default:
                availablePatterns.add(this::fireFollowingShotPattern);
                break;
        }
    }

    public BossEntity(GameContext context, int x, int y, int health, int cycle) {
        this(context, x, y, health, cycle, 0, false);
    }

    public BossEntity(GameContext context, int x, int y, int health) {
        this(context, x, y, health, 0, 0, false);
    }

    public BossEntity(GameContext context, int x, int y) {
        this(context, x, y, MAX_HEALTH, 0, 0, false);
    }

    private static String getBossSprite(int waveNumber, int cycle) {
        switch (waveNumber) {
            case 5: return "sprites/bosses/kraken_anim.gif";
            case 10: return "sprites/bosses/Hydra.png";
            case 15: return "sprites/bosses/Grifin.png";
            case 20: return "sprites/bosses/fireheart.png";
            case 25: return "sprites/bosses/endboss.png";
            default: return "sprites/boss_cycle" + cycle + ".gif";
        }
    }

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
        // Handle special states first, and return to prevent normal movement
        if (isTeleporting) {
            long timeSinceTeleport = System.currentTimeMillis() - teleportStartTime;
            if (timeSinceTeleport >= teleportTotalTime) {
                isTeleporting = false;
                int newX = new java.util.Random().nextInt(Game.GAME_WIDTH - getWidth());
                setX(newX);
                fireCirclePattern();
            }
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

        // Vertical bouncing for wave 10 boss
        if (waveNumber == 10) {
            if ((dy < 0) && (y < 0)) {
                dy = -dy;
            }
            if ((dy > 0) && (y > 250)) {
                dy = -dy;
            }
        }

        if (laserGimmickStartTime != 0 && System.currentTimeMillis() - laserGimmickStartTime > 3000) {
            LaserEntity laser = new LaserEntity(context, 0, Game.GAME_WIDTH);
            context.addEntity(laser);
            laserGimmicks.add(laser);
            laserGimmickStartTime = 0;
        }

        tryToFire();
        super.move(delta);
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        java.util.List<BossPattern> patternsToUse;

        if (waveNumber == 25) {
            patternsToUse = new java.util.ArrayList<>();
            switch (phase) {
                case 1:
                    patternsToUse.add(this::fireThreeWayPattern);
                    patternsToUse.add(this::fireCirclePattern);
                    patternsToUse.add(this::fireFollowingShotPattern);
                    patternsToUse.add(this::fireCurtainPattern);
                    break;
                case 2:
                    patternsToUse.add(this::fireTentacleAttackPattern);
                    patternsToUse.add(this::fireLaserSweepPattern);
                    break;
                case 3:
                    patternsToUse.add(this::spawnMinionsPattern);
                    patternsToUse.add(this::teleportAndBurstPattern);
                    break;
            }
        } else {
            patternsToUse = availablePatterns;
        }

        if (patternsToUse == null || patternsToUse.isEmpty()) {
            return;
        }

        // Select a random pattern from the list for the current phase
        java.util.List<BossPattern> selectablePatterns = new java.util.ArrayList<>(patternsToUse);
        if (lastUsedPattern != null && selectablePatterns.size() > 1) {
            selectablePatterns.remove(lastUsedPattern);
        }

        java.util.Random rand = new java.util.Random();
        BossPattern selectedPattern = selectablePatterns.get(rand.nextInt(selectablePatterns.size()));

        selectedPattern.execute();
        this.lastUsedPattern = selectedPattern;
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

    private void fireGlobalLaserPattern() {
        context.resetItemCollection();
        laserGimmickStartTime = System.currentTimeMillis();

        // Spawn two item entities at random x positions
        java.util.Random rand = new java.util.Random();
        context.addEntity(new ItemEntity(context, rand.nextInt(Game.GAME_WIDTH), 50));
        context.addEntity(new ItemEntity(context, rand.nextInt(Game.GAME_WIDTH), 50));
    }

    private void fireFeatherPattern() {
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

    private void fireFeatherStreamPattern() {
        isFiringFeatherStream = true;
        featherStreamCount = 0;
        lastFeatherShotTime = 0;
    }

    private void fireTentacleAttackPattern() {
        int numberOfAttacks = isMiniBoss ? 12 : 6;
        for (int i = 0; i < numberOfAttacks; i++) {
            // Spawn at a random location on the screen.
            // The area should be restricted to the game area.
            int randomX = (int) (Math.random() * (Game.GAME_WIDTH - 100)) + 50; // Avoid edges
            int randomY = (int) (Math.random() * (Game.GAME_HEIGHT - 200)) + 100; // Avoid top/bottom edges
            context.addEntity(new TentacleAttackEntity(context, randomX, randomY));
        }
    }

    private void splitIntoMiniBosses() {
        // Create two mini-bosses
        int miniBossHealth = (int) (health.getHp().getMAX_HP() / 2);
        int cycle = (waveNumber - 1) / 5;

        // Left mini-boss
        BossEntity miniBoss1 = new BossEntity(context, getX() - 50, getY(), miniBossHealth, cycle, waveNumber, true);
        context.addEntity(miniBoss1);

        // Right mini-boss
        BossEntity miniBoss2 = new BossEntity(context, getX() + 50, getY(), miniBossHealth, cycle, waveNumber, true);
        context.addEntity(miniBoss2);
    }

    private void spawnMinionsPattern() {
        int minionCount = 3;
        int spacing = 100;
        int startX = getX() + (getWidth() / 2) - ((minionCount - 1) * spacing / 2);

        for (int i = 0; i < minionCount; i++) {
            // Spawn minions in a line below the boss
            int minionX = startX + (i * spacing);
            int minionY = getY() + getHeight() + 50;
            context.addEntity(new AlienEntity(context, minionX, minionY));
        }
    }

    private void fireLaserSweepPattern() {
        double sweepSpeed = 200;
        // Randomly choose a sweep direction
        int direction = new java.util.Random().nextInt(4);
        switch (direction) {
            case 0: // From Left
                context.addEntity(new SweepingLaserEntity(context, -50, 0, sweepSpeed, 0));
                break;
            case 1: // From Right
                context.addEntity(new SweepingLaserEntity(context, Game.GAME_WIDTH + 50, 0, -sweepSpeed, 0));
                break;
            case 2: // From Top
                context.addEntity(new SweepingLaserEntity(context, 0, -50, 0, sweepSpeed));
                break;
            case 3: // From Bottom
                context.addEntity(new SweepingLaserEntity(context, 0, Game.GAME_HEIGHT + 50, 0, -sweepSpeed));
                break;
        }
    }

    private void teleportAndBurstPattern() {
        isTeleporting = true;
        teleportStartTime = System.currentTimeMillis();
    }


    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    if (!health.decreaseHealth(shot.getDamage())) {
                        context.removeEntity(this);
                        context.notifyAlienKilled(); // Notify for score and wave progression
                    } else {
                        // Phase transition for Wave 25 boss
                        if (waveNumber == 25) {
                            double maxHealth = health.getHp().getMAX_HP();
                            int currentHealth = health.getCurrentHealth();
                            if (phase == 1 && currentHealth <= maxHealth * 0.66) {
                                phase = 2;
                                // Optional: Add phase transition effect here
                            } else if (phase == 2 && currentHealth <= maxHealth * 0.33) {
                                phase = 3;
                                // Optional: Add phase transition effect here
                            }
                        }
                        // Splitting logic for Wave 20 boss
                        else if (waveNumber == 20 && !hasSplit && !isMiniBoss && health.getCurrentHealth() <= health.getHp().getMAX_HP() / 2) {
                            hasSplit = true;
                            splitIntoMiniBosses();
                            context.removeEntity(this); // Remove the main boss
                        }
                    }
                }
            }
        } else if (other instanceof LaserBeamEntity) {
            LaserBeamEntity laser = (LaserBeamEntity) other;
            if (health.isAlive()) {
                if (!health.decreaseHealth(laser.getDamage())) {
                    context.removeEntity(this);
                    context.removeEntity(laser);
                    context.notifyAlienKilled(); // Notify for score and wave progression
                } else {
                    // Phase transition for Wave 25 boss
                    if (waveNumber == 25) {
                        double maxHealth = health.getHp().getMAX_HP();
                        int currentHealth = health.getCurrentHealth();
                        if (phase == 1 && currentHealth <= maxHealth * 0.66) {
                            phase = 2;
                        } else if (phase == 2 && currentHealth <= maxHealth * 0.33) {
                            phase = 3;
                        }
                    }
                    // Splitting logic for Wave 20 boss
                    else if (waveNumber == 20 && !hasSplit && !isMiniBoss && health.getCurrentHealth() <= health.getHp().getMAX_HP() / 2) {
                        hasSplit = true;
                        splitIntoMiniBosses();
                        context.removeEntity(this); // Remove the main boss
                    }
                }
            }
        }
    }
}
