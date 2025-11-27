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

public class ThreeWayShooter extends Entity implements Enemy {
    private final double moveSpeed = 150; // 수평 이동에 맞게 조정됨
    private final GameContext context;
    private MovementPattern movementPattern;

    private long lastFire = 0;
    private final long firingInterval = 2000; // 2초마다 발사

    // 업그레이드 상태
    private boolean isUpgraded = false;
    private boolean specialShotPending = false;
    private long normalShotTime = 0;
    private static final long SPECIAL_SHOT_DELAY = 500; // 0.5초

    // 통합된 엔진 화염 효과
    private final Sprite[] fireFrames = new Sprite[3];
    private final long fireFrameDuration = 100; // ms
    private long fireLastFrameChange;
    private int fireFrameNumber;
    private final double fireSpriteScale = 0.8;

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

    public ThreeWayShooter(GameContext context, int x, int y) {
        this(context, x, y, MovementPattern.STRAIGHT_DOWN);
    }

    public void upgrade() {
        this.isUpgraded = true;
    }

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

    @Override
    public void onDestroy() {
        // 통합된 화염 효과에 대한 특별한 정리가 필요하지 않음
    }

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