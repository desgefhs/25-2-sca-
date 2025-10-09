package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;
import org.newdawn.spaceinvaders.player.BuffManager;
import org.newdawn.spaceinvaders.player.BuffType;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuffUI {

    private Map<BuffType, Sprite> buffIcons = new HashMap<>();

    public BuffUI() {
        buffIcons.put(BuffType.INVINCIBILITY, SpriteStore.get().getSprite("sprites/buff/invincible.gif"));
        buffIcons.put(BuffType.SPEED_BOOST, SpriteStore.get().getSprite("sprites/buff/speed.png"));
        buffIcons.put(BuffType.HEAL, SpriteStore.get().getSprite("sprites/buff/heal.png"));
    }

    public void draw(Graphics2D g, BuffManager buffManager) {
        if (buffManager == null) {
            return;
        }

        List<BuffType> activeBuffs = buffManager.getActiveBuffs();
        int xOffset = Game.GAME_WIDTH + 20;
        int yOffset = Game.GAME_HEIGHT - 50;
        int iconSize = 32;

        for (int i = 0; i < activeBuffs.size(); i++) {
            BuffType buff = activeBuffs.get(i);
            Sprite icon = buffIcons.get(buff);
            if (icon != null) {
                g.drawImage(icon.getImage(), xOffset + (i * (iconSize + 5)), yOffset, iconSize, iconSize, null);
            }
        }
    }
}
