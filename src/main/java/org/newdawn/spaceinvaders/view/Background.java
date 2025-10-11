package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics2D;

/**
 * 게임의 스크롤되는 배경을 관리하고 렌더링하는 클래스
 */
public class Background {
    private final Sprite backgroundSprite;
    /** 배경 스크롤 속도 (pixels/sec) */
    private final double moveSpeed = 50;
    private final int imageWidth;
    private final int imageHeight;
    /** 배경의 y축 오프셋 (스크롤 효과용) */
    private double yOffset;

    /**
     * 지정된 스프라이트로 배경 객체를 생성합
     *
     * @param spriteRef 배경으로 사용할 스프라이트의 참조 경로
     */
    public Background(String spriteRef) {
        this.backgroundSprite = SpriteStore.get().getSprite(spriteRef);
        this.imageWidth = this.backgroundSprite.getWidth();
        this.imageHeight = this.backgroundSprite.getHeight();
        this.yOffset = 0;
    }

    /**
     * 경과 시간에 따라 배경의 y-오프셋을 업데이트하여 스크롤 효과를 줌
     *
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    public void update(long delta) {
        // y-오프셋을 이미지 높이만큼 이동하면 다시 0으로 돌아가도록 하여 무한 스크롤 효과를 만듬
        yOffset = (yOffset + moveSpeed * (delta / 1000.0)) % imageHeight;
    }

    /**
     * 배경 이미지를 타일처럼 반복해서 그려 화면을 채움
     * y-오프셋을 적용하여 스크롤되는 것처럼 보이게
     *
     * @param g 그래픽 컨텍스트
     */
    public void draw(Graphics2D g) {
        if (imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        // 화면 너비와 높이를 채우기 위해 이미지를 반복해서 그림
        for (int x = 0; x < Game.GAME_WIDTH; x += imageWidth) {
            for (int y = (int) yOffset - imageHeight; y < Game.GAME_HEIGHT; y += imageHeight) {
                backgroundSprite.draw(g, x, y);
            }
        }
    }
}