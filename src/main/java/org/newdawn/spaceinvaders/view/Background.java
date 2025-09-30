package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics2D;

public class Background {

    private final Sprite backgroundSprite;
    private final double moveSpeed = 50; //
    private final int imageWidth;
    private final int imageHeight;
    private double yOffset;

    public Background(String spriteRef) {
        this.backgroundSprite = SpriteStore.get().getSprite(spriteRef);
        this.imageWidth = this.backgroundSprite.getWidth();
        this.imageHeight = this.backgroundSprite.getHeight();
        this.yOffset = 0;
    }

    public void update(long delta) {
        // 0에서 이미지 높이까지 움직이고 초기화
        yOffset = (yOffset + moveSpeed * (delta / 1000.0)) % imageHeight;
    }

    public void draw(Graphics2D g) {
        if (imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        // 위치 맞추기
        for (int x = 0; x < Game.GAME_WIDTH; x += imageWidth) {

            for (int y = (int) yOffset - imageHeight; y < Game.GAME_HEIGHT; y += imageHeight) {
                backgroundSprite.draw(g, x, y);
            }
        }
    }
}