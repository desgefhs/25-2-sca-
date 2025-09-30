package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics2D;

public class Background {

    private final Sprite backgroundSprite;
    private final double moveSpeed = 50; // pixels per second
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
        // The offset scrolls from 0 to the image height, then wraps around.
        yOffset = (yOffset + moveSpeed * (delta / 1000.0)) % imageHeight;
    }

    public void draw(Graphics2D g) {
        if (imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        // Tile the background both horizontally and vertically within the game area
        for (int x = 0; x < Game.GAME_WIDTH; x += imageWidth) { // Use GAME_WIDTH
            // Start drawing from one image height above the screen to ensure no gaps when wrapping.
            for (int y = (int) yOffset - imageHeight; y < Game.GAME_HEIGHT; y += imageHeight) { // Use GAME_HEIGHT
                backgroundSprite.draw(g, x, y);
            }
        }
    }
}