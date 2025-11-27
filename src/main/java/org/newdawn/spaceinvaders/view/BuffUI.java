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

/**
 * 플레이어에게 현재 적용 중인 버프 아이콘을 화면에 그리는 UI 클래스.
 */
public class BuffUI {

    /** 각 버프 타입과 그에 해당하는 아이콘 스프라이트를 매핑하는 맵. */
    private final Map<BuffType, Sprite> buffIcons = new HashMap<>();

    /**
     * BuffUI 생성자.
     * 각 버프 타입에 대한 아이콘을 미리 로드합니다.
     */
    public BuffUI() {
        buffIcons.put(BuffType.INVINCIBILITY, SpriteStore.get().getSprite("sprites/buff/invincible.gif"));
        buffIcons.put(BuffType.SPEED_BOOST, SpriteStore.get().getSprite("sprites/buff/speed.png"));
        buffIcons.put(BuffType.HEAL, SpriteStore.get().getSprite("sprites/buff/heal.png"));
    }

    /**
     * 현재 활성화된 모든 버프의 아이콘을 화면에 그립니다.
     * @param g 그래픽 컨텍스트
     * @param buffManager 플레이어의 버프를 관리하는 BuffManager
     */
    public void draw(Graphics2D g, BuffManager buffManager) {
        if (buffManager == null) {
            return;
        }

        List<BuffType> activeBuffs = buffManager.getActiveBuffs();
        int xOffset = Game.GAME_WIDTH + 20; // This seems to be off-screen, might be intended for a sidebar
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
