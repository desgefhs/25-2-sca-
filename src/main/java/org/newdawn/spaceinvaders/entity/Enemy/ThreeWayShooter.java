package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics;

/**
 * 3-Way 탄을 발사하는 적 엔티티
 * 강화 시 3-Way 탄 발사 후 추가로 유도탄을 발사
 */
public class ThreeWayShooter extends Entity implements Enemy {
    /** 이동 속도 */
    private double moveSpeed = 150;
    private GameContext context;
    /** 이동 패턴 */
    private MovementPattern movementPattern;

    /** 마지막 발사 시간 */
    private long lastFire = 0;
    /** 발사 간격 (2초) */
    private long firingInterval = 2000;

    // 강화 상태 관련
    /** 강화 상태 여부 */
    private boolean isUpgraded = false;
    /** 추가 유도탄 발사 대기 여부 */
    private boolean specialShotPending = false;
    /** 일반 3-Way 탄 발사 시간 */
    private long normalShotTime = 0;
    /** 추가 유도탄 발사 지연 시간 (0.5초) */
    private static final long SPECIAL_SHOT_DELAY = 500;

    // 엔진 불꽃 효과
    private final Sprite[] fireFrames = new Sprite[3];
    private final long fireFrameDuration = 100; // ms
    private long fireLastFrameChange;
    private int fireFrameNumber;
    private final double fireSpriteScale = 0.8;

    /**
     * ThreeWayShooter 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param x       x 좌표
     * @param y       y 좌표
     * @param pattern 이동 패턴
     */
    public ThreeWayShooter(GameContext context, int x, int y, MovementPattern pattern) {
        super("sprites/enemy/ThreeWayShooter.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this, 5);
        this.movementPattern = pattern;

        // 패턴에 따라 초기 속도 설정
        if (pattern == MovementPattern.HORIZ_TO_CENTER_AND_STOP) {
            this.dy = 0;
            this.dx = (x < Game.GAME_WIDTH / 2) ? moveSpeed : -moveSpeed;
        } else {
            this.movementPattern = MovementPattern.STRAIGHT_DOWN;
            this.dy = 100;
        }

        // 불꽃 프레임 미리 로드
        fireFrames[0] = SpriteStore.get().getSprite("sprites/fire effect/18 Ion.png");
        fireFrames[1] = SpriteStore.get().getSprite("sprites/fire effect/19 Ion.png");
        fireFrames[2] = SpriteStore.get().getSprite("sprites/fire effect/20 Ion.png");
    }


    /**
     * 엔티티를 강화 상태로 만듬
     */
    public void upgrade() {
        this.isUpgraded = true;
    }

    /**
     * 3-Way 탄을 발사합니다. 강화 상태일 경우 추가 유도탄 발사
     */
    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        ProjectileType type = ProjectileType.NORMAL_SHOT;
        double shotMoveSpeed = type.moveSpeed;
        double angle = Math.toRadians(30);

        // 중앙, 왼쪽, 오른쪽으로 3발 동시 발사
        context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width/2), getY() + height, 0, shotMoveSpeed));
        context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width/2), getY() + height, -Math.sin(angle) * shotMoveSpeed, Math.cos(angle) * shotMoveSpeed));
        context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width/2), getY() + height, Math.sin(angle) * shotMoveSpeed, Math.cos(angle) * shotMoveSpeed));

        // 강화 상태이면 추가 유도탄 발사 예약
        if (isUpgraded) {
            specialShotPending = true;
            normalShotTime = System.currentTimeMillis();
        }
    }

    @Override
    public void move(long delta) {
        // 특정 이동 패턴 처리
        if (movementPattern == MovementPattern.HORIZ_TO_CENTER_AND_STOP) {
            if (dx != 0 && Math.abs(x + (width/2.0) - (Game.GAME_WIDTH / 2.0)) < 10) {
                x = (dx > 0) ? (Game.GAME_WIDTH / 2.0f) - width : (Game.GAME_WIDTH / 2.0f);
                dx = 0;
                this.movementPattern = MovementPattern.STATIC; // 정지 상태로 변경
            }
        }

        super.move(delta);

        // 불꽃 애니메이션 업데이트
        fireLastFrameChange += delta;
        if (fireLastFrameChange > fireFrameDuration) {
            fireLastFrameChange = 0;
            fireFrameNumber = (fireFrameNumber + 1) % fireFrames.length;
        }

        tryToFire();

        // 예약된 추가 유도탄 발사 처리
        if (specialShotPending && System.currentTimeMillis() > normalShotTime + SPECIAL_SHOT_DELAY) {
            ProjectileType specialType = ProjectileType.FAST_FOLLOWING_SHOT;
            context.addEntity(new ProjectileEntity(context, specialType, 2, getX() + (width/2), getY() + height));
            specialShotPending = false; // 플래그 리셋
        }

        // 화면 밖으로 나가면 제거
        if (y > 600) {
            context.notifyAlienEscaped(this);
            context.removeEntity(this);
        }
    }

    @Override
    public void draw(Graphics g) {
        // 엔티티 뒤에 불꽃 효과를 먼저 그림
        Sprite fireSprite = fireFrames[fireFrameNumber];
        int fireWidth = (int) (fireSprite.getWidth() * fireSpriteScale);
        int fireHeight = (int) (fireSprite.getHeight() * fireSpriteScale);
        double fireX = this.x + (this.width / 2.0) - (fireWidth / 2.0);
        double fireY = this.y - fireHeight + 20;
        g.drawImage(fireSprite.getImage(), (int) fireX, (int) fireY, fireWidth, fireHeight, null);

        // 그 다음 엔티티 본체를 그림
        super.draw(g);
    }

    @Override
    public void onDestroy() {
        // 특별한 정리 작업 필요 없음
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity && ((ProjectileEntity) other).getType().targetType == ProjectileType.TargetType.ENEMY) {
            handleDamage(((ProjectileEntity) other).getDamage());
        } else if (other instanceof LaserBeamEntity) {
            handleDamage(((LaserBeamEntity) other).getDamage());
        }
    }

    /**
     * 데미지를 처리하고, 사망 시 폭발 효과를 생성
     * @param damage 받은 데미지
     */
    private void handleDamage(int damage) {
        if (health.isAlive()) {
            if (!health.decreaseHealth(damage)) {
                // 사망 시 폭발 효과 생성
                AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                explosion.setScale(0.1);
                int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
                explosion.setX(centeredX);
                explosion.setY(centeredY);
                context.addEntity(explosion);

                // 자신을 제거하고 처치 알림
                context.removeEntity(this);
                context.notifyAlienKilled();
            }
        }
    }
}
