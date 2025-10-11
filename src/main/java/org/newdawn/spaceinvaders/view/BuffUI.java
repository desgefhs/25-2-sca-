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
 * 플레이어의 활성화된 버프 아이콘을 화면에 표시하는 UI 클래스
 */
public class BuffUI {

    /** 버프 유형과 아이콘 스프라이트를 매핑하는 맵 */
    private Map<BuffType, Sprite> buffIcons = new HashMap<>();

    /**
     * BuffUI 객체를 생성하고, 각 버프 유형에 맞는 아이콘을 미리 로드
     */
    public BuffUI() {
        buffIcons.put(BuffType.INVINCIBILITY, SpriteStore.get().getSprite("sprites/buff/invincible.gif"));
        buffIcons.put(BuffType.SPEED_BOOST, SpriteStore.get().getSprite("sprites/buff/speed.png"));
        buffIcons.put(BuffType.HEAL, SpriteStore.get().getSprite("sprites/buff/heal.png"));
    }

    /**
     * 활성화된 모든 버프의 아이콘을 화면에 그림
     *
     * @param g           그래픽 컨텍스트
     * @param buffManager 버프 정보를 가져올 버프 매니저
     */
    public void draw(Graphics2D g, BuffManager buffManager) {
        if (buffManager == null) {
            return;
        }

        List<BuffType> activeBuffs = buffManager.getActiveBuffs();
        int xOffset = Game.GAME_WIDTH + 20; // 게임 영역 오른쪽에 표시
        int yOffset = Game.GAME_HEIGHT - 50; // 화면 하단에 표시
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
