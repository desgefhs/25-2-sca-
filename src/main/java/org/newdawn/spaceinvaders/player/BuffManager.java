package org.newdawn.spaceinvaders.player;

import org.newdawn.spaceinvaders.entity.ShipEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 플레이어의 버프를 관리하는 클래스
 * 버프 추가, 지속 시간 관리, 효과 적용 및 제거를 담당
 */
public class BuffManager {

    /**
     * 현재 활성화된 버프의 정보를 담는 내부 클래스
     */
    private static class ActiveBuff {
        BuffType type;
        long startTime;
        long duration; // 밀리초 단위

        ActiveBuff(BuffType type, long duration) {
            this.type = type;
            this.duration = duration;
            this.startTime = System.currentTimeMillis();
        }

        /**
         * 버프가 만료되었는지 확인
         * @return 만료되었으면 true
         */
        boolean isExpired() {
            return System.currentTimeMillis() - startTime > duration;
        }
    }

    private ShipEntity player;
    /** 현재 활성화된 버프 목록 */
    private List<ActiveBuff> activeBuffs = new ArrayList<>();

    public BuffManager(ShipEntity player) {
        this.player = player;
    }

    /**
     * 플레이어에게 새로운 버프를 추가
     * 이미 활성화된 버프인 경우, 지속 시간을 갱신
     *
     * @param type 추가할 버프의 유형
     */
    public void addBuff(BuffType type) {
        long duration = 0;
        switch (type) {
            case INVINCIBILITY:
            case SPEED_BOOST:
                duration = 3000; // 3초
                break;
            case DAMAGE_BOOST:
                duration = 3000; // 3초
                break;
            case HEAL:
                duration = 500; // 즉시 발동이지만, UI 표시를 위해 짧은 지속 시간 설정
                player.getHealth().fullyHeal();
                break;
        }

        // 이미 활성화된 버프이면 지속 시간만 갱신
        for (ActiveBuff buff : activeBuffs) {
            if (buff.type == type) {
                buff.startTime = System.currentTimeMillis();
                return;
            }
        }

        if (duration > 0) {
            activeBuffs.add(new ActiveBuff(type, duration));
            applyBuffEffect(type);
        }
    }

    /**
     * 활성화된 버프 목록을 업데이트하고, 만료된 버프를 제거
     */
    public void update() {
        Iterator<ActiveBuff> iterator = activeBuffs.iterator();
        while (iterator.hasNext()) {
            ActiveBuff buff = iterator.next();
            if (buff.isExpired()) {
                removeBuffEffect(buff.type);
                iterator.remove();
            }
        }
    }

    /**
     * 버프의 효과를 적용
     * @param type 적용할 버프 유형
     */
    private void applyBuffEffect(BuffType type) {
        if (type == BuffType.SPEED_BOOST) {
            player.setMoveSpeed(player.getMoveSpeed() * 1.5f);
        }
    }

    /**
     * 버프의 효과를 제거
     * @param type 제거할 버프 유형
     */
    private void removeBuffEffect(BuffType type) {
        if (type == BuffType.SPEED_BOOST) {
            player.setMoveSpeed(player.getMoveSpeed() / 1.5f);
        }
    }

    /**
     * 특정 버프가 활성화되어 있는지 확인
     *
     * @param type 확인할 버프 유형
     * @return 활성화 상태이면 true
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
     * 특정 버프의 레벨을 반환
     *
     * @param type 확인할 버프 유형
     * @return 버프 레벨, 없으면 0
     */
    public int getBuffLevel(BuffType type) {
        if (hasBuff(type)) {
            return 1;
        }
        return 0;
    }

    /**
     * 현재 활성화된 모든 버프의 유형 목록을 반환
     *
     * @return 활성화된 버프 유형 목록
     */
    public List<BuffType> getActiveBuffs() {
        List<BuffType> buffs = new ArrayList<>();
        for (ActiveBuff buff : activeBuffs) {
            buffs.add(buff.type);
        }
        return buffs;
    }
}
