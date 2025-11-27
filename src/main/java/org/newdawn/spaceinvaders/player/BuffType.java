package org.newdawn.spaceinvaders.player;

import org.newdawn.spaceinvaders.entity.ShipEntity;

public enum BuffType {
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

    private final long duration;

    BuffType(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public abstract void apply(ShipEntity player);
    public abstract void remove(ShipEntity player);
}
