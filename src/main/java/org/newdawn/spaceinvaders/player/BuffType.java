package org.newdawn.spaceinvaders.player;

import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 게임 내에서 사용되는 버프의 종류를 정의하는 열거형(Enum).
 * 각 버프는 지속 시간을 가지며, 플레이어에게 적용하고 제거하는 로직을 포함합니다.
 */
public enum BuffType {
    /**
     * 무적 버프. 플레이어를 일시적으로 모든 피해로부터 보호합니다.
     */
    INVINCIBILITY(3000) {
        @Override
        public void apply(ShipEntity player) {
            // 무적 효과 적용 로직 (미구현)
        }

        @Override
        public void remove(ShipEntity player) {
            // 무적 효과 제거 로직 (미구현)
        }
    },
    /**
     * 이동 속도 증가 버프. 플레이어의 이동 속도를 일시적으로 증가시킵니다.
     */
    SPEED_BOOST(3000) {
        private static final float SPEED_MULTIPLIER = 1.5f;

        @Override
        public void apply(ShipEntity player) {
            player.setMoveSpeed(player.getMoveSpeed() * SPEED_MULTIPLIER);
        }

        @Override
        public void remove(ShipEntity player) {
            player.setMoveSpeed(player.getMoveSpeed() / SPEED_MULTIPLIER);
        }
    },
    /**
     * 즉시 회복 버프. 플레이어의 체력을 즉시 최대로 회복시킵니다.
     */
    HEAL(500) {
        @Override
        public void apply(ShipEntity player) {
            player.getHealth().fullyHeal();
        }

        @Override
        public void remove(ShipEntity player) {
            // 즉시 발동 효과이므로 제거 로직은 필요 없음
        }
    },
    /**
     * 공격력 증가 버프. 플레이어의 공격력을 일시적으로 증가시킵니다.
     */
    DAMAGE_BOOST(3000) {
        @Override
        public void apply(ShipEntity player) {
            // 공격력 증가 효과 적용 로직 (미구현)
        }

        @Override
        public void remove(ShipEntity player) {
            // 공격력 증가 효과 제거 로직 (미구현)
        }
    };

    /** 버프의 지속 시간 (밀리초). */
    private final long duration;

    /**
     * BuffType 생성자.
     * @param duration 버프의 지속 시간
     */
    BuffType(long duration) {
        this.duration = duration;
    }

    /**
     * 버프의 지속 시간을 반환합니다.
     * @return 지속 시간 (밀리초)
     */
    public long getDuration() {
        return duration;
    }

    /**
     * 버프 효과를 플레이어에게 적용합니다.
     * @param player 버프를 적용할 플레이어 함선
     */
    public abstract void apply(ShipEntity player);

    /**
     * 버프 효과를 플레이어에게서 제거합니다.
     * @param player 버프를 제거할 플레이어 함선
     */
    public abstract void remove(ShipEntity player);
}
