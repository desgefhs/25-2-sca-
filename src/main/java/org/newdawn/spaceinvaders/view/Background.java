package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics2D;

/**
 * 게임의 배경 이미지를 그리고, 수직으로 무한 스크롤되는 효과를 처리하는 클래스.
 */
public class Background {

    /** 배경 이미지로 사용될 스프라이트. */
    private final Sprite backgroundSprite;
    /** 배경이 스크롤되는 속도 (픽셀/초). */
    private final double moveSpeed = 50;
    /** 배경 이미지의 너비. */
    private final int imageWidth;
    /** 배경 이미지의 높이. */
    private final int imageHeight;
    /** 수직 스크롤 오프셋. */
    private double yOffset;

    /**
     * Background 생성자.
     * @param spriteRef 배경 이미지로 사용할 스프라이트의 리소스 경로
     */
    public Background(String spriteRef) {
        this.backgroundSprite = SpriteStore.get().getSprite(spriteRef);
        this.imageWidth = this.backgroundSprite.getWidth();
        this.imageHeight = this.backgroundSprite.getHeight();
        this.yOffset = 0;
    }

    /**
     * 배경의 스크롤 위치를 업데이트합니다.
     * @param delta 마지막 프레임 이후 경과된 시간 (밀리초)
     */
    public void update(long delta) {
        // yOffset을 0에서 이미지 높이까지 계속 순환시켜 무한 스크롤 효과를 만듦
        yOffset = (yOffset + moveSpeed * (delta / 1000.0)) % imageHeight;
    }

    /**
     * 스크롤되는 배경을 화면에 그립니다.
     * 이미지를 타일처럼 이어붙여 화면을 채웁니다.
     * @param g 그래픽 컨텍스트
     */
    public void draw(Graphics2D g) {
        if (imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        // 화면 너비와 높이를 채우도록 이미지를 반복해서 그림
        for (int x = 0; x < Game.GAME_WIDTH; x += imageWidth) {
            for (int y = (int) yOffset - imageHeight; y < Game.GAME_HEIGHT; y += imageHeight) {
                backgroundSprite.draw(g, x, y);
            }
        }
    }
}