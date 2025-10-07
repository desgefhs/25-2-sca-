package org.newdawn.spaceinvaders.player;

import org.newdawn.spaceinvaders.entity.ShipEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BuffManager {

    private static class ActiveBuff {
        BuffType type;
        long startTime;
        long duration; // in milliseconds

        ActiveBuff(BuffType type, long duration) {
            this.type = type;
            this.duration = duration;
            this.startTime = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - startTime > duration;
        }
    }

    private ShipEntity player;
    private List<ActiveBuff> activeBuffs = new ArrayList<>();

    public BuffManager(ShipEntity player) {
        this.player = player;
    }

    public void addBuff(BuffType type) {
        long duration = 0;
        switch (type) {
            case INVINCIBILITY:
            case SPEED_BOOST:
                duration = 3000; // 3 seconds
                break;
            case DAMAGE_BOOST:
                duration = 3000; // 3 seconds
                break;
            case HEAL:
                duration = 500; // Instant, but show on UI for a short time
                player.getHealth().fullyHeal();
                break;
        }

        // If the buff is already active, refresh its duration
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

    private void applyBuffEffect(BuffType type) {
        if (type == BuffType.SPEED_BOOST) {
            player.setMoveSpeed(player.getMoveSpeed() * 1.5f);
        }
    }

    private void removeBuffEffect(BuffType type) {
        if (type == BuffType.SPEED_BOOST) {
            player.setMoveSpeed(player.getMoveSpeed() / 1.5f);
        }
    }

    public boolean hasBuff(BuffType type) {
        for (ActiveBuff buff : activeBuffs) {
            if (buff.type == type) {
                return true;
            }
        }
        return false;
    }

    public int getBuffLevel(BuffType type) {
        if (hasBuff(type)) {
            return 1; // For now, all buffs have a fixed level of 1
        }
        return 0;
    }

    public List<BuffType> getActiveBuffs() {
        List<BuffType> buffs = new ArrayList<>();
        for (ActiveBuff buff : activeBuffs) {
            buffs.add(buff.type);
        }
        return buffs;
    }
}
