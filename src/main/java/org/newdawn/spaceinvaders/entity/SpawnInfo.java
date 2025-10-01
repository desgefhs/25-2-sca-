package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.entity.EntityType;

public class SpawnInfo {
    public final EntityType entityType;
    public final int x;
    public final int y;

    public SpawnInfo(EntityType entityType, int x, int y) {
        this.entityType = entityType;
        this.x = x;
        this.y = y;
    }
}
