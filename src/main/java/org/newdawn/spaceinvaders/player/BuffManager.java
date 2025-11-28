package org.newdawn.spaceinvaders.player;


import org.newdawn.spaceinvaders.entity.ShipEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 플레이어(함선)에게 적용되는 버프를 관리하는 클래스.
 * 버프의 추가, 지속 시간 관리, 만료 시 제거 로직을 담당합니다.
 */
public class BuffManager {

    /**
     * 현재 활성화된 버프의 상태를 추적하는 내부 클래스.
     */
    private static class ActiveBuff {
        /** 버프의 종류. */
        BuffType type;
        /** 버프가 시작된 시간 (타임스탬프). */
        long startTime;
        /** 버프의 총 지속 시간 (밀리초). */
        long duration;

        /**
         * ActiveBuff 생성자.
         * @param type 적용할 버프의 타입
         */
        ActiveBuff(BuffType type) {
            this.type = type;
            this.duration = type.getDuration();
            this.startTime = System.currentTimeMillis();
        }

        /**
         * 버프가 만료되었는지 확인합니다.
         * @return 만료되었으면 true, 그렇지 않으면 false
         */
        boolean isExpired() {
            return System.currentTimeMillis() - startTime > duration;
        }
    }

    /** 버프의 효과를 받을 플레이어 함선 엔티티. */
    private final ShipEntity player;
    /** 현재 활성화된 버프 목록. */
    private final List<ActiveBuff> activeBuffs = new ArrayList<>();

    /**
     * BuffManager 생성자.
     * @param player 버프를 관리할 플레이어 함선
     */
    public BuffManager(ShipEntity player) {
        this.player = player;
    }

    /**
     * 플레이어에게 버프를 추가합니다.
     * 힐과 같이 즉시 적용되는 버프는 바로 효과를 적용하고, 지속 시간이 있는 버프는 목록에 추가하거나 지속 시간을 갱신합니다.
     *
     * @param type 추가할 버프의 타입
     */
    public void addBuff(BuffType type) {
        if (type == BuffType.HEAL) {
            type.apply(player);
            return;
        }

        for (ActiveBuff buff : activeBuffs) {
            if (buff.type == type) {
                buff.startTime = System.currentTimeMillis(); // 재적용, 지속시간 갱신
                return;
            }
        }

        activeBuffs.add(new ActiveBuff(type));
        type.apply(player);
    }

    /**
     * 활성화된 버프 목록을 업데이트합니다.
     * 만료된 버프를 찾아 효과를 제거하고 목록에서 삭제합니다.
     */
    public void update() {
        Iterator<ActiveBuff> iterator = activeBuffs.iterator();
        while (iterator.hasNext()) {
            ActiveBuff buff = iterator.next();
            if (buff.isExpired()) {
                buff.type.remove(player);
                iterator.remove();
            }
        }
    }


    /**
     * 특정 타입의 버프가 활성화 상태인지 확인합니다.
     *
     * @param type 확인할 버프의 타입
     * @return 버프가 활성화 상태이면 true, 그렇지 않으면 false
     */
    public boolean hasBuff(BuffType type) {
        for (ActiveBuff buff : activeBuffs) {
            if (buff.type == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * 현재 활성화된 모든 버프의 타입 목록을 반환합니다.
     *
     * @return 활성화된 버프 타입의 목록
     */
    public List<BuffType> getActiveBuffs() {
        List<BuffType> buffs = new ArrayList<>();
        for (ActiveBuff buff : activeBuffs) {
            buffs.add(buff.type);
        }
        return buffs;
    }
}
