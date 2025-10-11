package org.newdawn.spaceinvaders.entity;
import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Enemy.BombEntity;
import org.newdawn.spaceinvaders.entity.Enemy.Enemy;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.graphics.HpRender;
import org.newdawn.spaceinvaders.player.BuffManager;
import org.newdawn.spaceinvaders.player.BuffType;

import java.awt.*;

/**
 * 플레이어가 조종하는 우주선 엔티티
 * 이동, 발사, 체력, 버프, 충돌 등 플레이어와 관련된 모든 로직을 처리
 */
public class ShipEntity extends Entity {
    private GameContext context;
    private HpRender hpRender;
    /** 적과 충돌 시 받는 데미지 */
    private static final int COLLISION_DAMAGE = 1;

    /** 단기 무적 상태 여부 (피격 후) */
    private boolean invincible = false;
    /** 단기 무적 상태 남은 시간 */
    private long invincibilityTimer = 0;
    /** 단기 무적 상태 지속 시간 (0.5초) */
    private static final long INVINCIBILITY_DURATION = 500;

    /** 쉴드 보유 여부 */
    private boolean hasShield = false;
    /** 쉴드가 파괴되었을 때 실행될 콜백 */
    private Runnable onShieldBreak = null;

    /** 현재 장착된 무기 */
    private Weapon currentWeapon;
    /** 버프 매니저 */
    private BuffManager buffManager;
    /** 이동 속도 */
    private float moveSpeed = 300;

    /**
     * ShipEntity 객체를 생성
     *
     * @param context   게임 컨텍스트
     * @param ref       스프라이트 참조 경로
     * @param x         x 좌표
     * @param y         y 좌표
     * @param maxHealth 최대 체력
     */
	public ShipEntity(GameContext context,String ref,int x,int y, int maxHealth) {
		super(ref,x,y);
		this.health = new HealthComponent(this, maxHealth);
		this.context = context;
		this.hpRender = new HpRender(health.getHp());
        this.buffManager = new BuffManager(this);
    }

    /**
     * 버프를 활성화
     * @param level 버프 레벨
     * @param onEnd 버프 종료 시 실행될 콜백
     */
    public void activateBuff(int level, Runnable onEnd) {
        buffManager.addBuff(BuffType.DAMAGE_BOOST);
        onEnd.run();
    }

	public void setMaxHealth(int maxHealth) {
	    this.health = new HealthComponent(this, maxHealth);
	    this.hpRender = new HpRender(health.getHp());
	}

    @Override
	public void move(long delta) {
        // 버프 및 무적 상태 업데이트
        buffManager.update();
        if (invincible) {
            invincibilityTimer -= delta;
            if (invincibilityTimer <= 0) {
                invincible = false;
            }
        }

		super.move(delta);

        // 화면 밖으로 나가지 않도록 위치 제한
		if (x < 0) { x = 0; }
		if (x > Game.GAME_WIDTH - width) { x = Game.GAME_WIDTH - width; }
		if (y < 0) { y = 0; }
		if (y > Game.GAME_HEIGHT - height) { y = Game.GAME_HEIGHT - height; }
	}

    /**
     * 현재 무기를 설정
     * @param weapon 설정할 무기
     */
    public void setWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }

    /**
     * 현재 무기를 발사하려고 시도
     * 플레이어가 공격할 수 있는 상태일 때만 발사
     */
    public void tryToFire() {
        if (!context.canPlayerAttack()) {
            return;
        }
        currentWeapon.fire(context, this);
        context.getSoundManager().playSound(currentWeapon.getSoundName());
    }

    @Override
    public void draw(Graphics g) {
        int effectSize = Math.max(width, height) + 10;

        // 쉴드 시각 효과
        if (hasShield) {
            g.setColor(new Color(100, 100, 255, 70)); // 반투명 파란색
            g.fillOval((int) x - (effectSize - width) / 2, (int) y - (effectSize - height) / 2, effectSize, effectSize);
        }

        // 무적 상태일 때 깜빡이는 효과
	    boolean shouldDraw = true;
        if (isInvincible()) {
            if ((System.currentTimeMillis() / 100) % 2 == 0) {
                shouldDraw = false;
            }
        }

        if (shouldDraw) {
            super.draw(g);
        }

        hpRender.hpRender((Graphics2D) g, this);
    }

    @Override
    public void collidedWith(Entity other) {
        // 무적 상태일 때는 모든 충돌 무시
        if (isInvincible()) {
            return;
        }

        // 적과 충돌
        if (other instanceof Enemy) {
            // 폭탄 엔티티는 자체 폭발 로직이 있으므로 충돌 데미지 없음
            if (other instanceof BombEntity) {
                return;
            }

            // 다른 모든 적은 충돌 시 파괴됨
            context.removeEntity(other);
            context.notifyAlienKilled(); // 적 카운트 감소를 위해 알림

            // 우주선은 충돌 데미지를 입음
            if (!health.decreaseHealth(COLLISION_DAMAGE)) {
                context.notifyDeath();
            }
            return;
        }

        // 적의 발사체와 충돌
        if (other instanceof ProjectileEntity) {
            // 발사체 데미지는 HealthComponent에서 처리 (처리 후 자동 무적 부여)
            if (!health.decreaseHealth(((ProjectileEntity) other).getDamage())) {
                context.notifyDeath();
            }
        }
    }

    /**
     * 피격 후 짧은 시간 동안 무적 상태를 활성화
     */
    public void activateInvincibility() {
        invincible = true;
        invincibilityTimer = INVINCIBILITY_DURATION;
    }

    /**
     * 우주선의 상태를 초기화 (체력, 위치, 버프 등)
     */
    public void reset() {
        health.reset();
        invincible = false;
        invincibilityTimer = 0;
        setShield(false, null);
        buffManager = new BuffManager(this);
        x = Game.GAME_WIDTH / 2;
        y = 550;
    }

    public boolean hasShield() {
        return hasShield;
    }

    public void setShield(boolean hasShield, Runnable onBreak) {
        this.hasShield = hasShield;
        this.onShieldBreak = onBreak;
    }

    public BuffManager getBuffManager() {
        return buffManager;
    }

    public float getMoveSpeed() {
        return this.moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    /**
     * 현재 무적 상태인지 확인
     * @return 무적 상태이면 true
     */
    public boolean isInvincible() {
        return invincible || buffManager.hasBuff(BuffType.INVINCIBILITY);
    }

    /**
     * 체력을 회복
     * @param amount 회복할 체력량
     */
    public void heal(int amount) {
        health.increaseHealth(amount);
    }

    public int getMaxHealth() {
        return health.getMaxHp();
    }
}
