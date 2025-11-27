package org.newdawn.spaceinvaders.entity;


import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.Enemy.Enemy;
import org.newdawn.spaceinvaders.entity.Enemy.TentacleAttackEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.graphics.HpRender;

/**
 * 게임에 등장하는 보스 엔티티의 추상 기본 클래스.
 * 다양한 공격 패턴, 이동, 체력 관리, 충돌 처리 등 보스 엔티티의 공통적인 특성을 정의합니다.
 */
public abstract class BossEntity extends Entity implements Enemy {
    /**
     * 보스의 공격 패턴을 정의하는 함수형 인터페이스.
     * 이 인터페이스를 구현하는 람다식 또는 메소드는 보스의 특정 공격을 실행합니다.
     */
    @FunctionalInterface
    protected interface BossPattern {
        /** 보스의 공격 패턴을 실행합니다. */
        void execute();
    }

    /** 이 보스가 사용할 수 있는 공격 패턴 목록. */
    protected final java.util.List<BossPattern> availablePatterns = new java.util.ArrayList<>();
    /** 마지막으로 사용된 공격 패턴. */
    protected BossPattern lastUsedPattern = null;

    /** 게임 컨텍스트. */
    protected GameContext context;
    /** 마지막 발사 이후 시간. */
    private long lastFire = 0;
    /** 발사 간격 (밀리초). */
    private final long firingInterval;
    /** 보스의 HP 바를 그리는 렌더러. */
    private final HpRender hpRender;
    /** 깃털 스트림 공격 활성화 여부. */
    private boolean isFiringFeatherStream = false;
    /** 깃털 스트림 공격 시 발사된 횟수. */
    private int featherStreamCount = 0;
    /** 마지막 깃털 발사 시간. */
    private long lastFeatherShotTime = 0;
    /** 보스가 순간이동 중인지 여부. */
    private boolean isTeleporting = false;
    /** 순간이동 시작 시간 (타임스탬프). */
    private final long teleportStartTime = 0;
    /** 순간이동 시 보스가 보이지 않는 시간 (밀리초). */
    private final long teleportDisappearTime = 500;
    /** 레이저 기믹 시작 시간. */
    private long laserGimmickStartTime = 0;
    /** 무작위 패턴 선택 및 기타 무작위 로직에 사용될 난수 생성기. */
    private final java.util.Random random;

    /**
     * BossEntity 생성자.
     * @param context 게임 컨텍스트
     * @param sprite 보스 스프라이트 리소스 경로
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     * @param health 보스의 초기 체력
     */
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

    /**
     * 보스의 공격 패턴을 설정하는 추상 메소드.
     * 구체적인 보스 클래스에서 이를 구현하여 {@link #availablePatterns}를 채워야 합니다.
     */
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

    @Override
    public void move(long delta) {
        // 특수 상태를 먼저 처리하고, 일반적인 움직임을 막기 위해 반환
        if (isTeleporting) {
            handleTeleportation();
            return; // 보스는 보이지 않고 움직이지 않음
        }

        if (isFiringFeatherStream) {
            handleFeatherStreamAttack();
        }

        // 표준 이동 및 공격
        // 수평으로 튕김
        if ((dx < 0) && (x < 0)) {
            dx = -dx;
        }
        if ((dx > 0) && (x > Game.GAME_WIDTH - width)) {
            dx = -dx;
        }

        // 레이저 기믹 타이머 처리
        if (laserGimmickStartTime != 0 && System.currentTimeMillis() - laserGimmickStartTime > 3000) {
            context.addEntity(new LaserEntity(context, 0, Game.GAME_WIDTH));
            laserGimmickStartTime = 0;
        }

        tryToFire();
        super.move(delta);
    }

    /** 순간이동 로직을 처리합니다. */
    private void handleTeleportation() {
        final long teleportTotalTime = 1000; // 보스가 다시 나타나 발사할 때까지의 시간(ms)
        if (System.currentTimeMillis() - teleportStartTime >= teleportTotalTime) {
            isTeleporting = false;
            int newX = random.nextInt(Game.GAME_WIDTH - getWidth());
            setX(newX);
            fireCirclePattern(); // 순간이동 후 특정 패턴 실행
        }
    }

    /** 깃털 스트림 공격 로직을 처리합니다. */
    private void handleFeatherStreamAttack() {
        final long featherShotDelay = 100;
        final int featherStreamSize = 5;

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

    /**
     * 발사 간격에 따라 공격 패턴을 시도합니다.
     * 사용 가능한 패턴 중에서 무작위로 선택하여 실행하며, 마지막에 사용한 패턴은 가급적 피합니다.
     */
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

    /** 커튼 패턴 공격: 화면 상단에서 안전 지대를 제외하고 발사체를 발사합니다. */
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

    /** 3방향으로 발사체를 발사하는 패턴. */
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

    /** 원형으로 발사체를 퍼뜨리는 패턴. */
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

    /** 플레이어를 추적하는 발사체를 발사하는 패턴. */
    protected void fireFollowingShotPattern() {
        ProjectileType type = ProjectileType.FOLLOWING_SHOT;
        int damage = 2;

        // 3발의 확산탄 발사
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 20, getY() + 50));
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 50, getY() + 50));
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + 80, getY() + 50));
    }

    /** 화면 전체에 걸쳐 레이저를 발사하는 기믹을 시작하고, 아이템을 스폰합니다. */
    protected void fireGlobalLaserPattern() {
        context.resetItemCollection();
        laserGimmickStartTime = System.currentTimeMillis();

        // 무작위 x 위치에 2개의 아이템 엔티티 생성
        context.addEntity(new ItemEntity(context, random.nextInt(Game.GAME_WIDTH), 50));
        context.addEntity(new ItemEntity(context, random.nextInt(Game.GAME_WIDTH), 50));
    }

    /** 부채꼴 모양으로 깃털 발사체를 발사하는 패턴. */
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

    /** 깃털 스트림 공격을 활성화합니다. */
    protected void fireFeatherStreamPattern() {
        isFiringFeatherStream = true;
        featherStreamCount = 0;
        lastFeatherShotTime = 0;
    }

    /** 촉수 공격 패턴을 실행합니다. */
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

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * @param other 충돌한 다른 엔티티
     */
    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            handleProjectileCollision((ProjectileEntity) other);
        } else if (other instanceof LaserBeamEntity) {
            handleLaserCollision((LaserBeamEntity) other);
        }
    }

    /** 발사체와의 충돌을 처리합니다. */
    private void handleProjectileCollision(ProjectileEntity shot) {
        if (shot.getType().targetType == ProjectileType.TargetType.ENEMY && health.isAlive()) {
            if (!health.decreaseHealth(shot.getDamage())) {
                this.destroy();
            }
        }
    }

    /** 레이저 빔과의 충돌을 처리합니다. */
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