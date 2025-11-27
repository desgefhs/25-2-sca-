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
    private final long firingInterval; // 2.5초마다 발사
    private final HpRender hpRender;
    private boolean isFiringFeatherStream = false;
    private int featherStreamCount = 0;
    private long lastFeatherShotTime = 0;
    private boolean isTeleporting = false;
    private final long teleportStartTime = 0;
    private final long teleportDisappearTime = 500; // 보스가 보이지 않는 시간(ms)
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
        this.random = new java.util.Random(); // 무작위 인스턴스 초기화

        setupPatterns();
    }

    protected abstract void setupPatterns();

    @Override
    public void draw(java.awt.Graphics g) {
        if (isTeleporting && System.currentTimeMillis() - teleportStartTime < teleportDisappearTime) {
            // 보이지 않는 동안 그리지 않음
            return;
        }
        super.draw(g);
        hpRender.hpRender((java.awt.Graphics2D) g, this);
    }

    public void move(long delta) {
        final long teleportTotalTime = 1000; // 보스가 다시 나타나 발사할 때까지의 시간(ms)
        final int featherStreamSize = 5;
        final long featherShotDelay = 100;

        // 특수 상태를 먼저 처리하고, 일반적인 움직임을 막기 위해 반환
        if (isTeleporting) {
            handleTeleportation();
            return; // 보스는 보이지 않고 움직이지 않음
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

        // 표준 이동 및 공격
        // 수평으로 튕김
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
        final long teleportTotalTime = 1000; // 보스가 다시 나타나 발사할 때까지의 시간(ms)
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

        // 현재 단계에 대한 목록에서 무작위 패턴 선택
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
        int gapWidth = 100; // 안전 지대의 너비
        int gapPosition = random.nextInt(Game.GAME_WIDTH - gapWidth);
        int projectileWidth = 10; // 발사체 스프라이트의 대략적인 너비

        for (int x = 0; x < Game.GAME_WIDTH; x += projectileWidth) {
            if (x > gapPosition && x < gapPosition + gapWidth) {
                continue; // 간격에 발사체 생성을 건너뜀
            }
            context.addEntity(new ProjectileEntity(context, type, damage, x, 0, 0, shotMoveSpeed));
        }
    }

    protected void fireThreeWayPattern() {
        ProjectileType type = ProjectileType.NORMAL_SHOT;
        int damage = 1;
        double shotMoveSpeed = type.moveSpeed;
        double angle = Math.toRadians(30);

        // 중앙 발사 (0도)
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, 0, shotMoveSpeed));

        // 왼쪽 발사 (-30도)
        double dxLeft = -Math.sin(angle) * shotMoveSpeed;
        double dyLeft = Math.cos(angle) * shotMoveSpeed;
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, dxLeft, dyLeft));

        // 오른쪽 발사 (+30도)
        double dxRight = Math.sin(angle) * shotMoveSpeed;
        double dyRight = Math.cos(angle) * shotMoveSpeed;
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, dxRight, dyRight));
    }

    protected void fireCirclePattern() {
        ProjectileType type = ProjectileType.NORMAL_SHOT;
        int damage = 1;
        int numShots = 12; // 원형 발사의 발사체 수
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

        // 3발의 확산탄 발사
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 20, getY() + 50));
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 50, getY() + 50));
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 80, getY() + 50));
    }

    protected void fireGlobalLaserPattern() {
        context.resetItemCollection();
        laserGimmickStartTime = System.currentTimeMillis();

        // 무작위 x 위치에 2개의 아이템 엔티티 생성
        context.addEntity(new ItemEntity(context, random.nextInt(Game.GAME_WIDTH), 50));
        context.addEntity(new ItemEntity(context, random.nextInt(Game.GAME_WIDTH), 50));
    }

    protected void fireFeatherPattern() {
        ProjectileType type = ProjectileType.FEATHER_SHOT;
        int damage = 1;
        double shotMoveSpeed = type.moveSpeed;
        int numShots = 5; // 부채꼴 모양으로 5발 발사
        double fanAngle = Math.toRadians(90); // 90도 부채꼴

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
            // 화면의 무작위 위치에 생성합니다.
            // 영역은 게임 영역으로 제한되어야 합니다.
            int randomX = (int) (random.nextDouble() * (Game.GAME_WIDTH - 100)) + 50; // 가장자리 피하기
            int randomY = (int) (Math.random() * (Game.GAME_HEIGHT - 200)) + 100; // 위/아래 가장자리 피하기
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
        // 이 엔티티는 업그레이드할 수 없습니다.
    }
}