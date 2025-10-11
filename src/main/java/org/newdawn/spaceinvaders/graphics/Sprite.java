package org.newdawn.spaceinvaders.graphics;

import java.awt.Graphics;
import java.awt.Image;

/**
 * 게임 내에서 사용되는 이미지(스프라이트)를 표현하는 클래스
 * AWT의 Image 객체를 래핑하여 너비, 높이 정보 및 그리기 기능을 제공
 */
public class Sprite {

	private Image image;

	/**
	 * Image 객체로부터 새로운 Sprite를 생성
	 *
	 * @param image 이 스프라이트에 사용할 이미지
	 */
	public Sprite(Image image) {
		this.image = image;
	}

	public int getWidth() {
		return image.getWidth(null);
	}

	/**
	 * 스프라이트의 높이를 반환
	 *
	 * @return 높이 (픽셀)
	 */
	public int getHeight() {
		return image.getHeight(null);
	}

	/**
	 * 지정된 위치에 스프라이트를 그림
	 *
	 * @param g 그래픽 컨텍스트
	 * @param x x 좌표
	 * @param y y 좌표
	 */
	public void draw(Graphics g,int x,int y) {
	    g.drawImage(image,x,y,null);
	}

	/**
	 * 지정된 위치에 지정된 크기로 스프라이트를 그림
	 *
	 * @param g      그래픽 컨텍스트
	 * @param x      x 좌표
	 * @param y      y 좌표
	 * @param width  그릴 너비
	 * @param height 그릴 높이
	 */
	public void draw(Graphics g, int x, int y, int width, int height) {
	    g.drawImage(image, x, y, width, height, null);
	}

	public Image getImage() {
	    return image;
	}
}