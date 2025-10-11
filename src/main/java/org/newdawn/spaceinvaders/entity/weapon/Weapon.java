package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.player.PlayerStats;

public interface Weapon {
    void fire(GameContext context, ShipEntity owner);
    void upgrade(PlayerStats stats);
    void setLevel(int level);
    String getSoundName();
}
