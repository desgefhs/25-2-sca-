package org.newdawn.spaceinvaders.entity.boss;


import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Enemy.Enemy;
import org.newdawn.spaceinvaders.entity.Enemy.AlienEntity;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.HealthComponent;
import org.newdawn.spaceinvaders.entity.ItemEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.graphics.HpRender;

/**
 * 게임의 보스 적을 나타내는 엔티티 클래스
 * 웨이브 번호에 따라 다른 공격 패턴, 체력, 외형을 가짐
 */
public class BossEntity extends Entity implements Enemy {
    /**
     * 보스 패턴을 정의하는 함수형 인터페이스
     */
    @FunctionalInterface
    private interface BossPattern {
        void execute();
    }

    /** 현재 보스가 사용할 수 있는 공격 패턴 목록 */
    private final java.util.List<BossPattern> availablePatterns = new java.util.ArrayList<>();
    /** 마지막으로 사용한 공격 패턴 */
    private BossPattern lastUsedPattern = null;
    /** 마지막 특수 공격 시간 */
    private long lastSpecialAttack = 0;

    /** 이동 속도 */
    private double moveSpeed = 50;
    /** 수직 이동 속도 */
    private double dy = 50;
    /** 게임 컨텍스트 */
    private GameContext context;
    /** 기본 최대 체력 */
    private static final int MAX_HEALTH = 50;
    /** 마지막 발사 시간 */
    private long lastFire = 0;
    /** 발사 간격 */
    private long firingInterval = 2500;
    /** HP 렌더러 */
    private HpRender hpRender;
    /** 현재 웨이브 번호 */
    private int waveNumber;

    // 보스 상태 관련 필드
    /** 5 웨이브 보스의 상태 (공격, 아이템 생성, 레이저 충전) */
    private enum Boss5State { ATTACKING, SPAWNING_ITEMS, CHARGING_LASER }
    private Boss5State boss5State = Boss5State.ATTACKING;
    private long stateTimer = 0;
    /** 깃털 스트림 발사 중인지 여부 */
    private boolean isFiringFeatherStream = false;
    private int featherStreamCount = 0;
    private long lastFeatherShotTime = 0;
    private final int featherStreamSize = 5;
    private final long featherShotDelay = 100;
    /** 20 웨이브 보스가 분열했는지 여부 */
    private boolean hasSplit = false;
    /** 미니 보스인지 여부 */
    private boolean isMiniBoss = false;
    /** 25 웨이브 보스의 페이즈 */
    private int phase = 1;
    /** 텔레포트 중인지 여부 */
    private boolean isTeleporting = false;
    private long teleportStartTime = 0;
    private final long teleportDisappearTime = 500; // 텔레포트 시 사라져 있는 시간
    private final long teleportTotalTime = 1000;    // 텔레포트 후 다시 나타나 공격하기까지의 총 시간
    /** 레이저 기믹용 레이저 엔티티 목록 */
    private final java.util.List<LaserEntity> laserGimmicks = new java.util.ArrayList<>();
    private long laserGimmickStartTime = 0;


    /**
     * BossEntity 객체를 생성
     *
     * @param context    게임 컨텍스트
     * @param x          x 좌표
     * @param y          y 좌표
     * @param health     체력
     * @param cycle      보스 사이클 (외형 결정용)
     * @param waveNumber 웨이브 번호
     * @param isMiniBoss 미니 보스 여부
     */
    public BossEntity(GameContext context, int x, int y, int health, int cycle, int waveNumber, boolean isMiniBoss) {
        super(getBossSprite(waveNumber, cycle), x, y);
        this.context = context;
        this.health = new HealthComponent(this,health);
        this.hpRender = new HpRender(this.health.getHp());
        this.waveNumber = waveNumber;
        this.isMiniBoss = isMiniBoss;
        this.firingInterval = 2500; // 2.5초 발사 간격
        dx = -moveSpeed;
        dy = (waveNumber == 10) ? moveSpeed : 0; // 10 웨이브 보스만 수직 이동

        setScale(isMiniBoss ? 1.5 : 2.5);

        if (waveNumber == 5) {
            stateTimer = System.currentTimeMillis();
            context.resetItemCollection();
        }
        setupPatterns();
    }

    /**
     * 웨이브 번호에 따라 사용 가능한 공격 패턴을 설정
     */
    private void setupPatterns() {
        switch (waveNumber) {
            case 5: // 크라켄
                availablePatterns.add(this::fireCirclePattern);
                availablePatterns.add(this::fireThreeWayPattern);
                availablePatterns.add(this::fireGlobalLaserPattern);
                break;
            case 10: // 히드라
                availablePatterns.add(this::fireFollowingShotPattern);
                availablePatterns.add(this::fireCurtainPattern);
                break;
            case 15: // 그리핀
                availablePatterns.add(this::fireFeatherPattern);
                availablePatterns.add(this::fireFeatherStreamPattern);
                break;
            case 20: // 파이어하트
                availablePatterns.add(this::fireTentacleAttackPattern);
                break;
            case 25: // 최종 보스
                availablePatterns.add(this::fireCirclePattern);
                availablePatterns.add(this::fireThreeWayPattern);
                availablePatterns.add(this::fireGlobalLaserPattern);
                availablePatterns.add(this::fireFollowingShotPattern);
                availablePatterns.add(this::fireCurtainPattern);
                availablePatterns.add(this::fireFeatherPattern);
                availablePatterns.add(this::fireFeatherStreamPattern);
                availablePatterns.add(this::fireTentacleAttackPattern);
                break;
            default: // 기타
                availablePatterns.add(this::fireFollowingShotPattern);
                break;
        }
    }


    /**
     * 웨이브 번호에 따라 보스의 스프라이트 경로를 반환
     * @param waveNumber 웨이브 번호
     * @param cycle      기본 보스 사이클
     * @return 스프라이트 경로 문자열
     */
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
        // 텔레포트 중 투명 상태일 때는 그리지 않음
        if (isTeleporting && System.currentTimeMillis() - teleportStartTime < teleportDisappearTime) {
            return;
        }
        super.draw(g);
        hpRender.hpRender((java.awt.Graphics2D) g, this);
    }

    @Override
    public void move(long delta) {
        // 특수 상태(텔레포트, 깃털 스트림)를 먼저 처리
        if (isTeleporting) {
            long timeSinceTeleport = System.currentTimeMillis() - teleportStartTime;
            if (timeSinceTeleport >= teleportTotalTime) {
                isTeleporting = false;
                int newX = new java.util.Random().nextInt(Game.GAME_WIDTH - getWidth());
                setX(newX);
                fireCirclePattern(); // 텔레포트 후 원형 패턴 발사
            }
            return; // 텔레포트 중에는 이동 및 공격 안 함
        }

        if (isFiringFeatherStream) {
            if (System.currentTimeMillis() - lastFeatherShotTime > featherShotDelay) {
                if (featherStreamCount < featherStreamSize) {
                    ProjectileType type = ProjectileType.FEATHER_SHOT;
                    context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width / 2), getY() + (height / 2), 0, type.moveSpeed));
                    lastFeatherShotTime = System.currentTimeMillis();
                    featherStreamCount++;
                } else {
                    isFiringFeatherStream = false;
                }
            }
        }

        // 기본 좌우 이동 (화면 끝에서 튕김)
        if ((dx < 0) && (x < 0)) {
            dx = -dx;
        }
        if ((dx > 0) && (x > Game.GAME_WIDTH - width)) {
            dx = -dx;
        }

        // 10 웨이브 보스의 상하 이동
        if (waveNumber == 10) {
            if ((dy < 0) && (y < 0)) {
                dy = -dy;
            }
            if ((dy > 0) && (y > 250)) {
                dy = -dy;
            }
        }

        // 레이저 기믹 타이머
        if (laserGimmickStartTime != 0 && System.currentTimeMillis() - laserGimmickStartTime > 3000) {
            LaserEntity laser = new LaserEntity(context, 0, Game.GAME_WIDTH);
            context.addEntity(laser);
            laserGimmicks.add(laser);
            laserGimmickStartTime = 0;
        }

        tryToFire();
        super.move(delta);
    }

    /**
     * 발사 간격에 따라 현재 사용 가능한 공격 패턴 중 하나를 무작위로 실행
     */
    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        if (availablePatterns.isEmpty()) {
            return;
        }

        // 이전에 사용한 패턴을 제외하고 선택 가능한 패턴 목록 생성
        java.util.List<BossPattern> selectablePatterns = new java.util.ArrayList<>(availablePatterns);
        if (lastUsedPattern != null && selectablePatterns.size() > 1) {
            selectablePatterns.remove(lastUsedPattern);
        }

        // 패턴 무작위 선택 및 실행
        java.util.Random rand = new java.util.Random();
        BossPattern selectedPattern = selectablePatterns.get(rand.nextInt(selectablePatterns.size()));
        selectedPattern.execute();
        this.lastUsedPattern = selectedPattern;
    }

    // --- 공격 패턴 메소드들 ---


    /** 3-Way 발사 패턴 (5 웨이브 - 크라켄) */
    private void fireThreeWayPattern() {
        ProjectileType type = ProjectileType.NORMAL_SHOT;
        double shotMoveSpeed = type.moveSpeed;
        double angle = Math.toRadians(30);

        // 중앙
        context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width/2), getY() + height, 0, shotMoveSpeed));
        // 왼쪽
        context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width/2), getY() + height, -Math.sin(angle) * shotMoveSpeed, Math.cos(angle) * shotMoveSpeed));
        // 오른쪽
        context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width/2), getY() + height, Math.sin(angle) * shotMoveSpeed, Math.cos(angle) * shotMoveSpeed));
    }

    /** 원형 발사 패턴 (5 웨이브 - 크라켄) */
    private void fireCirclePattern() {
        ProjectileType type = ProjectileType.NORMAL_SHOT;
        int numShots = 12;
        double shotMoveSpeed = type.moveSpeed;

        for (int i = 0; i < numShots; i++) {
            double angle = Math.toRadians(360.0 / numShots * i);
            double dx = Math.sin(angle) * shotMoveSpeed;
            double dy = Math.cos(angle) * shotMoveSpeed;
            context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width / 2), getY() + (height / 2), dx, dy));
        }
    }

    /** 전역 레이저 패턴 (5 웨이브 - 크라켄) */
    private void fireGlobalLaserPattern() {
        context.resetItemCollection();
        laserGimmickStartTime = System.currentTimeMillis();

        // 아이템 2개 무작위 위치에 생성
        java.util.Random rand = new java.util.Random();
        context.addEntity(new ItemEntity(context, rand.nextInt(Game.GAME_WIDTH), 50));
        context.addEntity(new ItemEntity(context, rand.nextInt(Game.GAME_WIDTH), 50));
    }

    /** 탄막 커튼 패턴 (10 웨이브 - 히드라) */
    private void fireCurtainPattern() {
        ProjectileType type = ProjectileType.HYDRA_CURTAIN;
        int gapWidth = 100; // 안전지대 너비
        int gapPosition = new java.util.Random().nextInt(Game.GAME_WIDTH - gapWidth);
        int projectileWidth = 10; // 발사체 스프라이트 너비

        for (int x = 0; x < Game.GAME_WIDTH; x += projectileWidth) {
            if (x <= gapPosition || x >= gapPosition + gapWidth) {
                context.addEntity(new ProjectileEntity(context, type, 1, x, 0, 0, type.moveSpeed));
            }
        }
    }

    /** 유도탄 발사 패턴 (10 웨이브 - 히드라) */
    private void fireFollowingShotPattern() {
        ProjectileType type = ProjectileType.FOLLOWING_SHOT;
        context.addEntity(new ProjectileEntity(context, type, 2, getX() + 20, getY() + 50));
        context.addEntity(new ProjectileEntity(context, type, 2, getX() + 50, getY() + 50));
        context.addEntity(new ProjectileEntity(context, type, 2, getX() + 80, getY() + 50));
    }


    /** 깃털 부채꼴 발사 패턴 (15 웨이브 - 그리핀) */
    private void fireFeatherPattern() {
        ProjectileType type = ProjectileType.FEATHER_SHOT;
        int numShots = 5;
        double fanAngle = Math.toRadians(90);
        double startAngle = -fanAngle / 2;
        double angleStep = fanAngle / (numShots - 1);

        for (int i = 0; i < numShots; i++) {
            double angle = startAngle + i * angleStep;
            double dx = Math.sin(angle) * type.moveSpeed;
            double dy = Math.cos(angle) * type.moveSpeed;
            context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width / 2), getY() + (height / 2), dx, dy));
        }
    }

    /** 깃털 스트림 발사 패턴 (15 웨이브 - 그리핀) */
    private void fireFeatherStreamPattern() {
        isFiringFeatherStream = true;
        featherStreamCount = 0;
        lastFeatherShotTime = 0;
    }

    /** 촉수 공격 패턴 (20 웨이브 - 파이어하트) */
    private void fireTentacleAttackPattern() {
        int numberOfAttacks = isMiniBoss ? 12 : 6;
        for (int i = 0; i < numberOfAttacks; i++) {
            int randomX = (int) (Math.random() * (Game.GAME_WIDTH - 100)) + 50;
            int randomY = (int) (Math.random() * (Game.GAME_HEIGHT - 200)) + 100;
            context.addEntity(new TentacleAttackEntity(context, randomX, randomY));
        }
    }

    /** 미니 보스로 분열 (20 웨이브 - 파이어하트) */
    private void splitIntoMiniBosses() {
        int miniBossHealth = (int) (health.getHp().getMAX_HP() / 2);
        int cycle = (waveNumber - 1) / 5;

        BossEntity miniBoss1 = new BossEntity(context, getX() - 50, getY(), miniBossHealth, cycle, waveNumber, true);
        context.addEntity(miniBoss1);

        BossEntity miniBoss2 = new BossEntity(context, getX() + 50, getY(), miniBossHealth, cycle, waveNumber, true);
        context.addEntity(miniBoss2);
    }

    /** 부하 소환 패턴 */
    private void spawnMinionsPattern() {
        int minionCount = 3;
        int spacing = 100;
        int startX = getX() + (getWidth() / 2) - ((minionCount - 1) * spacing / 2);

        for (int i = 0; i < minionCount; i++) {
            int minionX = startX + (i * spacing);
            int minionY = getY() + getHeight() + 50;
            context.addEntity(new AlienEntity(context, minionX, minionY));
        }
    }

    /** 레이저 스윕 패턴 */
    private void fireLaserSweepPattern() {
        double sweepSpeed = 200;
        int direction = new java.util.Random().nextInt(4);
        switch (direction) {
            case 0: context.addEntity(new SweepingLaserEntity(context, -50, 0, sweepSpeed, 0)); break; // Left
            case 1: context.addEntity(new SweepingLaserEntity(context, Game.GAME_WIDTH + 50, 0, -sweepSpeed, 0)); break; // Right
            case 2: context.addEntity(new SweepingLaserEntity(context, 0, -50, 0, sweepSpeed)); break; // Top
            case 3: context.addEntity(new SweepingLaserEntity(context, 0, Game.GAME_HEIGHT + 50, 0, -sweepSpeed)); break; // Bottom
        }
    }

    /** 텔레포트 후 버스트 공격 패턴 */
    private void teleportAndBurstPattern() {
        isTeleporting = true;
        teleportStartTime = System.currentTimeMillis();
    }


    @Override
    public void collidedWith(Entity other) {
        // 발사체와 충돌 처리
        if (other instanceof ProjectileEntity && ((ProjectileEntity) other).getType().targetType == ProjectileType.TargetType.ENEMY) {
            handleDamage(((ProjectileEntity) other).getDamage());
            context.removeEntity(other); // 발사체 제거
        }
        // 플레이어 레이저 빔과 충돌 처리
        else if (other instanceof LaserBeamEntity) {
            handleDamage(((LaserBeamEntity) other).getDamage());
            // 레이저 빔은 관통하므로 제거하지 않음
        }
    }

    /**
     * 데미지를 처리하고, 체력에 따라 페이즈 전환 또는 분열을 처리
     * @param damage 받은 데미지
     */
    private void handleDamage(int damage) {
        if (!health.isAlive()) return;

        if (!health.decreaseHealth(damage)) {
            // 보스 사망
            context.removeEntity(this);
            context.notifyAlienKilled();
        } else {
            // 페이즈 전환 및 분열 로직
            if (waveNumber == 25) { // 최종 보스 페이즈
                double maxHealth = health.getHp().getMAX_HP();
                int currentHealth = health.getCurrentHealth();
                if (phase == 1 && currentHealth <= maxHealth * 0.66) {
                    phase = 2;
                } else if (phase == 2 && currentHealth <= maxHealth * 0.33) {
                    phase = 3;
                }
            } else if (waveNumber == 20 && !hasSplit && !isMiniBoss && health.getCurrentHealth() <= health.getHp().getMAX_HP() / 2) {
                // 20 웨이브 보스 분열
                hasSplit = true;
                splitIntoMiniBosses();
                context.removeEntity(this); // 원본 보스 제거
            }
        }
    }
    @Override
    public void upgrade() {
        // 보스는 업그레이드되지 않음
    }
}
