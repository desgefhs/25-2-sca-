package org.newdawn.spaceinvaders.entity;
import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Enemy.BombEntity;
import org.newdawn.spaceinvaders.entity.Enemy.Enemy;
import org.newdawn.spaceinvaders.entity.Pet.PetEntity;
import org.newdawn.spaceinvaders.entity.Pet.PetType;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.graphics.HpRender;
import org.newdawn.spaceinvaders.player.BuffManager;
import org.newdawn.spaceinvaders.player.BuffType;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어가 직접 조종하는 함선(Ship)을 표현하는 엔티티입니다.
 * 이동, 발사, 체력, 버프, 충돌 등 플레이어와 관련된 모든 핵심 로직을 포함하고 있습니다.
 */
public class ShipEntity extends Entity {
    /** 함선의 HP 바를 그리는 렌더러. */
    private HpRender hpRender;
    /** 다른 엔티티와 충돌 시 받는 기본 데미지. */
    private static final int COLLISION_DAMAGE = 1;

    /** 단시간의 기본 무적 상태 여부. */
    private boolean invincible = false;
    /** 기본 무적 상태의 남은 시간. */
    private long invincibilityTimer = 0;
    /** 기본 무적 상태의 지속 시간 (밀리초). */
    private static final long INVINCIBILITY_DURATION = 500; // 0.5초

    /** 방어막(쉴드) 활성화 여부. */
    private boolean hasShield = false;
    /** 쉴드가 파괴될 때 실행될 콜백. */
    private Runnable onShieldBreak = null;

    /** 버프 활성화 여부 (레거시 코드, {@link BuffManager}로 대체됨). */
    private boolean isBuffActive = false;
    /** 버프 타이머 (레거시 코드). */
    private long buffTimer = 0;
    /** 버프 지속 시간 (레거시 코드). */
    private static final long BUFF_DURATION = 3000; // 3초
    /** 버프 레벨 (레거시 코드). */
    private final int buffLevel = 0;
    /** 버프 종료 콜백 (레거시 코드). */
    private final Runnable onBuffEnd = null;

    /** 현재 장착된 무기. */
    private Weapon currentWeapon;
    /** 현재 활성화된 펫 목록. */
    private final Map<PetType, PetEntity> activePets = new HashMap<>();

    /** 함선의 버프 상태를 관리하는 매니저. */
    private BuffManager buffManager;
    /** 함선의 이동 속도. */
    private float moveSpeed = 300;

    /**
     * ShipEntity 생성자.
     * @param context 게임 컨텍스트
     * @param ref 스프라이트 리소스 경로
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
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
     * @deprecated {@link BuffManager} 사용으로 대체됨.
     */
    @Deprecated
    public void activateBuff(int level, Runnable onEnd) {
        buffManager.addBuff(BuffType.DAMAGE_BOOST);
        onEnd.run();
    }

    /**
     * 함선의 최대 체력을 재설정합니다.
     * @param maxHealth 새로운 최대 체력
     */
	public void setMaxHealth(int maxHealth) {
	    this.health = new HealthComponent(this, maxHealth);
	    this.hpRender = new HpRender(health.getHp());
	}

    /**
     * 함선을 이동시키고 상태를 업데이트합니다.
     * 이동 범위를 화면 내로 제한하고, 무적 및 버프 타이머를 갱신합니다.
     * @param delta 마지막 프레임 이후 경과 시간
     */
    @Override
	public void move(long delta) {
        buffManager.update();
        if (invincible) {
            invincibilityTimer -= delta;
            if (invincibilityTimer <= 0) {
                invincible = false;
            }
        }
        // 레거시 버프 로직
        if (isBuffActive) {
            buffTimer -= delta;
            if (buffTimer <= 0) {
                isBuffActive = false;
                if (onBuffEnd != null) {
                    onBuffEnd.run();
                }
            }
        }
        super.move(delta);

        // 화면 밖으로 나가지 않도록 위치 보정
		if (x < 0) { x = 0; }
		if (x > Game.GAME_WIDTH - width) { x = Game.GAME_WIDTH - width; }
		if (y < 0) { y = 0; }
		if (y > Game.GAME_HEIGHT - height) { y = Game.GAME_HEIGHT - height; }
	}

    /**
     * 함선의 현재 무기를 설정합니다.
     * @param weapon 장착할 무기
     */
    public void setWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }

    /**
     * 현재 무기를 발사하도록 시도합니다.
     * 공격 가능 상태일 때만 발사됩니다.
     */
    public void tryToFire() {
        if (!context.canPlayerAttack()) {
            return;
        }
        currentWeapon.fire(context, this);
        context.getGameContainer().getSoundManager().playSound(currentWeapon.getSoundName());
    }

    /**
     * 함선을 화면에 그립니다.
     * 쉴드, 버프, 무적 상태에 따라 시각적 효과(원, 깜빡임)를 추가로 표시합니다.
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void draw(Graphics g) {
        int effectSize = Math.max(width, height) + 10;

        if (hasShield) {
            g.setColor(new Color(100, 100, 255, 70)); // 반투명 파란색
            g.fillOval((int) x - (effectSize - width) / 2, (int) y - (effectSize - height) / 2, effectSize, effectSize);
        }
        if (isBuffActive) {
            g.setColor(new Color(255, 100, 100, 70)); // 반투명 빨간색
            g.fillOval((int) x - (effectSize - width) / 2, (int) y - (effectSize - height) / 2, effectSize, effectSize);
        }

	    boolean shouldDraw = true;
        // 무적 상태일 때 깜빡이는 효과
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

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (isInvincible()) {
            return;
        }

        if (other instanceof Enemy) {
            if (other instanceof BombEntity) {
                return; // 폭탄 엔티티는 자체 폭발 로직으로 처리
            }
            other.destroy(); // 적은 충돌 시 파괴됨
            if (!health.decreaseHealth(COLLISION_DAMAGE)) {
                this.destroy(); // 함선은 데미지를 입음
            }
            return;
        }

        if (other instanceof ProjectileEntity) {
            // 발사체 데미지는 HealthComponent에서 처리 (무적 시간 부여 포함)
            if (!health.decreaseHealth(((ProjectileEntity) other).getDamage())) {
                this.destroy();
            }
        }
    }

    /**
     * 짧은 시간 동안 기본 무적 상태를 활성화합니다.
     */
    public void activateInvincibility() {
        invincible = true;
        invincibilityTimer = INVINCIBILITY_DURATION;
    }

    /**
     * 함선의 상태를 초기값으로 재설정합니다.
     * 체력, 위치, 버프, 쉴드 등 모든 상태를 리셋합니다.
     */
    @Override
    public void reset() {
        super.reset();
        health.reset();
        invincible = false;
        invincibilityTimer = 0;
        setShield(false, null);
        buffManager = new BuffManager(this);
        x = Game.GAME_WIDTH / 2.0;
        y = 550;
    }

    /**
     * 함선에 쉴드가 활성화되어 있는지 확인합니다.
     * @return 쉴드가 있으면 true
     */
    public boolean hasShield() {
        return hasShield;
    }

    /**
     * 함선의 쉴드 상태를 설정합니다.
     * @param hasShield 쉴드 활성화 여부
     * @param onBreak 쉴드가 파괴되었을 때 실행될 콜백
     */
    public void setShield(boolean hasShield, Runnable onBreak) {
        this.hasShield = hasShield;
        this.onShieldBreak = onBreak;
    }

    /**
     * 함선의 버프 관리자를 반환합니다.
     * @return 버프 관리자
     */
    public BuffManager getBuffManager() {
        return buffManager;
    }

    /**
     * 함선의 현재 이동 속도를 반환합니다.
     * @return 이동 속도
     */
    public float getMoveSpeed() {
        return this.moveSpeed;
    }

    /**
     * 함선의 이동 속도를 설정합니다.
     * @param moveSpeed 새로운 이동 속도
     */
    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    /**
     * 함선이 현재 무적 상태인지 확인합니다.
     * (기본 무적 또는 버프로 인한 무적)
     * @return 무적 상태이면 true
     */
    public boolean isInvincible() {
        return invincible || buffManager.hasBuff(BuffType.INVINCIBILITY);
    }

    /**
     * 함선의 체력을 회복합니다.
     * @param amount 회복할 체력의 양
     */
    public void heal(int amount) {
        health.increaseHealth(amount);
    }

    /**
     * 함선의 최대 체력을 반환합니다.
     * @return 최대 체력
     */
    public int getMaxHealth() {
        return health.getMaxHp();
    }
}
