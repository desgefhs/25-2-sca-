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
 * 세 방향으로 발사체를 발사하는 적 엔티티.
 * 지정된 이동 패턴에 따라 움직이며, 업그레이드 시 특수 발사체를 추가로 발사합니다.
 */
public class ThreeWayShooter extends Entity implements Enemy {
    /** 수평 이동 속도. */
    private final double moveSpeed = 150;
    /** 게임 컨텍스트. */
    private final GameContext context;
    /** 이 슈터의 이동 패턴. */
    private MovementPattern movementPattern;

    /** 마지막 발사 이후 시간. */
    private long lastFire = 0;
    /** 발사 간격 (밀리초). */
    private final long firingInterval = 2000;

    /** 이 슈터가 업그레이드되었는지 여부. */
    private boolean isUpgraded = false;
    /** 특수 발사체가 대기 중인지 여부. */
    private boolean specialShotPending = false;
    /** 일반 발사 시간. */
    private long normalShotTime = 0;
    /** 특수 발사 지연 시간 (밀리초). */
    private static final long SPECIAL_SHOT_DELAY = 500;

    // 통합된 엔진 화염 효과 관련 필드
    /** 화염 애니메이션 프레임 배열. */
    private final Sprite[] fireFrames = new Sprite[3];
    /** 각 화염 프레임의 지속 시간 (밀리초). */
    private final long fireFrameDuration = 100;
    /** 마지막 화염 프레임 변경 이후 경과 시간. */
    private long fireLastFrameChange;
    /** 현재 화염 애니메이션 프레임 번호. */
    private int fireFrameNumber;
    /** 화염 스프라이트의 크기 배율. */
    private final double fireSpriteScale = 0.8;

    /**
     * ThreeWayShooter 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     * @param pattern 이 슈터가 사용할 이동 패턴
     */
    public ThreeWayShooter(GameContext context, int x, int y, MovementPattern pattern) {
        super("sprites/enemy/ThreeWayShooter.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this, 5); // 예시 체력
        this.movementPattern = pattern;

        // 패턴에 따라 초기 속도 설정
        if (pattern == MovementPattern.HORIZ_TO_CENTER_AND_STOP) {
            this.dy = 0;
            if (x < Game.GAME_WIDTH / 2) {
                this.dx = moveSpeed; // 오른쪽으로 이동
            } else {
                this.dx = -moveSpeed; // 왼쪽으로 이동
            }
        } else {
            // 기본 동작
            this.movementPattern = MovementPattern.STRAIGHT_DOWN;
            this.dy = 100;
        }

        // 모든 화염 프레임 미리 로드
        fireFrames[0] = SpriteStore.get().getSprite("sprites/fire effect/18 Ion.png");
        fireFrames[1] = SpriteStore.get().getSprite("sprites/fire effect/19 Ion.png");
        fireFrames[2] = SpriteStore.get().getSprite("sprites/fire effect/20 Ion.png");
    }

    /**
     * ThreeWayShooter의 오버로드된 생성자. 기본 이동 패턴은 STRAIGHT_DOWN입니다.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     */
    public ThreeWayShooter(GameContext context, int x, int y) {
        this(context, x, y, MovementPattern.STRAIGHT_DOWN);
    }

    /**
     * 이 엔티티를 업그레이드 상태로 만듭니다.
     * 업그레이드된 슈터는 일반 발사 후 지연된 특수 발사체를 발사합니다.
     */
    @Override
    public void upgrade() {
        this.isUpgraded = true;
    }

    /**
     * 발사 간격에 따라 3방향 발사체를 발사하려고 시도합니다.
     * 업그레이드된 슈터는 일반 발사 후 지연된 특수 발사를 예약합니다.
     */
    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

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

        if (isUpgraded) {
            specialShotPending = true;
            normalShotTime = System.currentTimeMillis();
        }
    }

    /**
     * 엔티티의 이동, 애니메이션, 발사 로직을 업데이트합니다.
     * @param delta 마지막 업데이트 이후 경과 시간
     */
    @Override
    public void move(long delta) {
        if (movementPattern == MovementPattern.HORIZ_TO_CENTER_AND_STOP) {
            // 이동 중이고 중앙에 도달하면 멈춤
            if (dx != 0) {
                float centerX = Game.GAME_WIDTH / 2.0f;
                // 중앙에 가까운지 확인
                if (Math.abs(x + (width/2) - centerX) < 10) {
                    if (dx > 0) { // 오른쪽으로 이동 중이었음
                        x = centerX - width; // 중앙의 왼쪽에 멈춤
                    } else { // 왼쪽으로 이동 중이었음
                        x = centerX; // 중앙의 오른쪽에 멈춤
                    }
                    dx = 0;
                    this.movementPattern = MovementPattern.STATIC; // 정지 상태가 됨
                }
            }
        }

        super.move(delta);

        // 화염 애니메이션 업데이트
        fireLastFrameChange += delta;
        if (fireLastFrameChange > fireFrameDuration) {
            fireLastFrameChange = 0;
            fireFrameNumber = (fireFrameNumber + 1) % fireFrames.length;
        }

        tryToFire();

        // 대기 중인 지연된 특수 발사 처리
        if (specialShotPending && System.currentTimeMillis() > normalShotTime + SPECIAL_SHOT_DELAY) {
            ProjectileType specialType = ProjectileType.FAST_FOLLOWING_SHOT;
            int specialDamage = 2; // 또는 적절한 피해량
            context.addEntity(new ProjectileEntity(context, specialType, specialDamage, getX() + (width/2), getY() + height));
            specialShotPending = false; // 플래그 재설정
        }

        // 화면 하단을 벗어나면 자신을 파괴
        if (y > 600) {
            this.destroy();
        }
    }

    /**
     * 슈터 엔티티와 함께 화염 효과를 그립니다.
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void draw(Graphics g) {
        // 화염 효과를 먼저 그려서 엔티티 뒤에 있도록 함
        Sprite fireSprite = fireFrames[fireFrameNumber];
        int fireWidth = (int) (fireSprite.getWidth() * fireSpriteScale);
        int fireHeight = (int) (fireSprite.getHeight() * fireSpriteScale);
        double fireX = this.x + (this.width / 2.0) - (fireWidth / 2.0);
        double fireY = this.y - fireHeight + 20; // 위쪽 후방에 위치시킴
        g.drawImage(fireSprite.getImage(), (int) fireX, (int) fireY, fireWidth, fireHeight, null);

        // 이제 부모 클래스의 회전 로직을 사용하여 엔티티 자체를 그림
        super.draw(g);
    }

    /**
     * 엔티티가 제거될 때 호출됩니다.
     * 이 클래스에서는 특별한 정리 로직이 필요하지 않습니다.
     */
    @Override
    public void onDestroy() {
        // 특별한 정리가 필요하지 않습니다.
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 발사체나 레이저 빔과 충돌 시 체력을 감소시키고, 체력이 0이 되면 파괴됩니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                handleDamage(shot.getDamage());
            }
        } else if (other instanceof LaserBeamEntity) {
            LaserBeamEntity laser = (LaserBeamEntity) other;
            handleDamage(laser.getDamage());
        }
    }

    /**
     * 데미지를 처리하고 체력이 0이 되면 엔티티를 파괴합니다.
     * @param damage 받을 데미지 양
     */
    private void handleDamage(int damage) {
        if (health.isAlive()) {
            if (!health.decreaseHealth(damage)) {
                // 폭발을 생성하고, 크기를 조절하며, 발사체의 중앙에 위치시킴
                AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                explosion.setScale(0.1);
                int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
                explosion.setX(centeredX);
                explosion.setY(centeredY);
                context.addEntity(explosion);

                this.destroy();
            }
        }
    }
}