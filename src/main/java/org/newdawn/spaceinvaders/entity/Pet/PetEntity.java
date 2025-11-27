package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 펫 엔티티의 추상 기본 클래스.
 * 플레이어 함선을 따라다니고, 특정 능력(Ability)을 주기적으로 사용하는 공통 로직을 처리합니다.
 */
public abstract class PetEntity extends Entity {

    /** 펫이 상호작용할 게임 컨텍스트. */
    protected final GameContext game;
    /** 펫이 따라다닐 플레이어 함선. */
    protected final ShipEntity player;
    /** 플레이어 함선과의 x축 오프셋. */
    private final int offsetX;

    /** 마지막으로 능력을 사용한 시간. */
    private long lastAbilityTime = 0;
    /** 능력의 재사용 대기시간 (밀리초). */
    protected long abilityCooldown;
    /** 펫의 현재 레벨. */
    protected int level;

    /**
     * 새로운 펫 엔티티를 생성합니다.
     * @param game 펫이 존재하는 게임 컨텍스트
     * @param player 펫이 따라다닐 플레이어 함선
     * @param ref 펫 스프라이트의 리소스 경로
     * @param x 초기 x 위치
     * @param y 초기 y 위치
     * @param initialLevel 펫의 초기 레벨
     */
    public PetEntity(GameContext game, ShipEntity player, String ref, int x, int y, int initialLevel) {
        super(ref, x, y);
        this.game = game;
        this.player = player;
        this.offsetX = player.getWidth(); // 펫을 플레이어 함선 오른쪽에 위치시킴
        this.setLevel(initialLevel);
    }

    /**
     * 엔티티를 이동시키고 능력 활성화를 처리합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        handleMovement(delta);
        handleAbilityActivation(delta);
    }

    /**
     * 펫의 이동 로직을 처리하여 플레이어를 따라다니게 합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    protected void handleMovement(long delta) {
        // 플레이어를 따라 오프셋을 적용하여 이동
        this.x = player.getX() + offsetX;
        this.y = player.getY();

        super.move(delta); // 기본 엔티티 이동 로직 호출
    }

    /**
     * 펫 능력의 재사용 대기시간이 지났으면 능력을 활성화합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (현재 사용되지 않음)
     */
    protected void handleAbilityActivation(long delta) {
        // 재사용 대기시간이 지나면 펫의 능력을 활성화
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAbilityTime > getAbilityCooldown()) {
            lastAbilityTime = currentTime;
            activateAbility();
        }
    }

    /**
     * 펫 능력의 현재 재사용 대기시간을 반환합니다.
     * 하위 클래스에서 동적인 값으로 오버라이드할 수 있습니다.
     * @return 능력 재사용 대기시간 (밀리초)
     */
    protected long getAbilityCooldown() {
        return this.abilityCooldown;
    }

    /**
     * 펫은 다른 엔티티와 충돌하지 않습니다. (추상 메소드 구현)
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        // 펫은 기본적으로 다른 엔티티와 충돌하지 않습니다.
    }

    /**
     * 각 펫의 고유 능력을 활성화하는 추상 메소드.
     * 하위 클래스에서 구체적인 능력을 구현해야 합니다.
     */
    public abstract void activateAbility();

    /**
     * 펫 능력의 재사용 대기시간 타이머를 리셋합니다.
     */
    public void resetAbilityCooldown() {
        this.lastAbilityTime = System.currentTimeMillis();
    }

    /**
     * 펫의 레벨을 설정하고, 이에 따라 펫의 능력치를 업데이트합니다.
     * @param level 펫의 새로운 레벨
     */
    public void setLevel(int level) {
        this.level = level;
        updateStatsByLevel();
    }

    /**
     * 펫의 현재 레벨에 따라 고유 능력치를 업데이트하는 추상 메소드.
     * 하위 클래스에서 구현해야 합니다.
     */
    protected abstract void updateStatsByLevel();
}
