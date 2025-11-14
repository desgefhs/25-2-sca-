package org.newdawn.spaceinvaders.player;


import org.newdawn.spaceinvaders.entity.ShipEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BuffManager {

    private static class ActiveBuff {
        BuffType type;
        long startTime;
        long duration;

        ActiveBuff(BuffType type) {
            this.type = type;
            this.duration = type.getDuration();
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
        if (type == BuffType.HEAL) {
            type.apply(player);
            return;
        }

        for (ActiveBuff buff : activeBuffs) {
            if (buff.type == type) {
                buff.startTime = System.currentTimeMillis();
                return;
            }
        }

        activeBuffs.add(new ActiveBuff(type));
        type.apply(player);
    }

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


    public boolean hasBuff(BuffType type) {
        for (ActiveBuff buff : activeBuffs) {
            if (buff.type == type) {
                return true;
            }
        }
        return false;
    }

    public List<BuffType> getActiveBuffs() {
        List<BuffType> buffs = new ArrayList<>();
        for (ActiveBuff buff : activeBuffs) {
            buffs.add(buff.type);
        }
        return buffs;
    }
}
