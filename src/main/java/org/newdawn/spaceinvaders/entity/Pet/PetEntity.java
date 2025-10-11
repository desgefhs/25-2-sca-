package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 펫 엔티티를 위한 추상 기본 클래스
 * 플레이어를 따라다니는 공통 로직을 처리
 */
public abstract class PetEntity extends Entity {

    protected final GameContext game; // Game context for the entity to interact with the game
    /** 따라다닐 플레이어 우주선 엔티티 */
    protected final ShipEntity player;
    /** 플레이어로부터의 x축 오프셋 */
    private final int offsetX;

    /** 마지막으로 능력을 사용한 시간 */
    private long lastAbilityTime = 0;
    /** 능력 재사용 대기시간 (밀리초) */
    protected final long abilityCooldown;

    /**
     * 새로운 펫 엔티티를 생성
     *
     * @param game           펫이 존재할 게임 컨텍스트
     * @param player         따라다닐 플레이어 우주선
     * @param ref            이 엔티티의 스프라이트 참조 경로
     * @param x              초기 x 좌표
     * @param y              초기 y 좌표
     * @param cooldown       능력 재사용 대기시간 (밀리초)
     */
    public PetEntity(GameContext game, ShipEntity player, String ref, int x, int y, long cooldown) {
        super(ref, x, y);
        this.game = game;
        this.player = player;
        this.offsetX = player.getWidth(); // 플레이어 오른쪽에 위치하도록 오프셋 설정
        this.abilityCooldown = cooldown;
    }

    /**
     * 경과된 시간에 따라 엔티티를 이동
     * 플레이어를 따라다니며, 재사용 대기시간이 되면 능력을 활성화
     *
     * @param delta 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        // 오프셋을 두고 플레이어를 따라다님
        this.x = player.getX() + offsetX;
        this.y = player.getY();

        super.move(delta);

        // 재사용 대기시간이 지났으면 능력 활성화
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAbilityTime > getAbilityCooldown()) {
            lastAbilityTime = currentTime;
            activateAbility();
        }
    }

    /**
     * 펫 능력의 재사용 대기시간을 가져
     * 하위 클래스에서 동적인 값을 위해 재정의할 수 있음
     * @return 능력 재사용 대기시간 (밀리초)
     */
    protected long getAbilityCooldown() {
        return this.abilityCooldown;
    }

    @Override
    public void collidedWith(Entity other) {
        // 펫은 현재 다른 엔티티와 충돌하지 않음
    }

    /**
     * 각 펫 하위 클래스는 자신만의 특별한 능력을 구현
     */
    public abstract void activateAbility();

    /**
     * 펫 능력의 재사용 대기시간 타이머를 리셋
     */
    public void resetAbilityCooldown() {
        this.lastAbilityTime = System.currentTimeMillis();
    }
}
