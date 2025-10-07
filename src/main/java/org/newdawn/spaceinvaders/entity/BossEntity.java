package org.newdawn.spaceinvaders.entity;


import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.graphics.HpRender;

public class BossEntity extends Entity {
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
    private final java.util.List<LaserEntity> laserGimmicks = new java.util.ArrayList<>();
    private long laserGimmickStartTime = 0;


    public BossEntity(GameContext context, int x, int y, int health, int cycle, int waveNumber) {
        super(waveNumber == 15 ? "sprites/bosses/Grifin.png" : (waveNumber == 10 ? "sprites/bosses/Hydra.png" : (waveNumber == 5 ? "sprites/bosses/kraken_anim.gif" : "sprites/boss_cycle" + cycle + ".gif")), x, y);
        this.context = context;
        this.health = new HealthComponent(this,health);
        this.hpRender = new HpRender(this.health.getHp());
        this.waveNumber = waveNumber;
        this.firingInterval = 2500; // 2.5초 딜레이
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
                break;
            case 20:
                availablePatterns.add(this::fireCurtainPattern);
                availablePatterns.add(this::fireThreeWayPattern);
                break;
            case 25:
                availablePatterns.add(this::fireThreeWayPattern);
                availablePatterns.add(this::fireCirclePattern);
                availablePatterns.add(this::fireFollowingShotPattern);
                availablePatterns.add(this::fireCurtainPattern);
                break;
            default:
                availablePatterns.add(this::fireFollowingShotPattern);
                break;
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

        if (availablePatterns.isEmpty()) {
            return;
        }

        // 마지막에 사용한 패턴을 제외한 새로운 목록 생성
        java.util.List<BossPattern> selectablePatterns = new java.util.ArrayList<>(availablePatterns);
        if (lastUsedPattern != null) {
            selectablePatterns.remove(lastUsedPattern);
        }

        // 만약 필터링된 목록이 비어있으면 (예: 패턴이 1개뿐인 경우), 전체 목록을 다시 사용
        if (selectablePatterns.isEmpty()) {
            selectablePatterns.addAll(availablePatterns);
        }

        // 선택 가능한 패턴 목록에서 무작위로 하나를 선택
        java.util.Random rand = new java.util.Random();
        BossPattern selectedPattern = selectablePatterns.get(rand.nextInt(selectablePatterns.size()));

        // 패턴 실행 및 마지막 사용 패턴으로 기록
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
        } else if (other instanceof LaserBeamEntity) {
            LaserBeamEntity laser = (LaserBeamEntity) other;
            if (health.isAlive()) {
                if (!health.decreaseHealth(laser.getDamage())) {
                    context.removeEntity(this);
                    context.removeEntity(laser);
                    context.notifyAlienKilled(); // Notify for score and wave progression
                }
            }
        }
    }
}
