package org.newdawn.spaceinvaders.graphics;

import java.awt.Graphics;
import java.awt.Image;

/**
 * 게임 내에서 사용되는 이미지(스프라이트)를 나타내는 클래스.
 * AWT의 {@link Image} 객체를 감싸고, 너비, 높이 정보 및 그리기 기능을 제공합니다.
 */
public class Sprite {

	/** 이 스프라이트가 나타내는 원본 이미지 객체. */
	private final Image image;

	/**
	 * Sprite 생성자.
	 * @param image 이 스프라이트가 사용할 Image 객체
	 */
	public Sprite(Image image) {
		this.image = image;
	}

	/**
	 * 스프라이트의 너비를 반환합니다.
	 * @return 이미지의 너비 (픽셀)
	 */
	public int getWidth() {
		return image.getWidth(null);
	}

	/**
	 * 스프라이트의 높이를 반환합니다.
	 * @return 이미지의 높이 (픽셀)
	 */
	public int getHeight() {
		return image.getHeight(null);
	}

	/**
	 * 지정된 위치에 스프라이트를 원본 크기로 그립니다.
	 * @param g 그래픽 컨텍스트
	 * @param x 스프라이트를 그릴 x 좌표
	 * @param y 스프라이트를 그릴 y 좌표
	 */
	public void draw(Graphics g,int x,int y) {
		g.drawImage(image,x,y,null);
	}

	/**
	 * 지정된 위치에 스프라이트를 지정된 크기로 그립니다.
	 * @param g 그래픽 컨텍스트
	 * @param x 스프라이트를 그릴 x 좌표
	 * @param y 스프라이트를 그릴 y 좌표
	 * @param width 그릴 너비
	 * @param height 그릴 높이
	 */
	public void draw(Graphics g, int x, int y, int width, int height) {
		g.drawImage(image, x, y, width, height, null);
	}

	/**
	 * 이 스프라이트의 원본 Image 객체를 반환합니다.
	 * @return Image 객체
	 */
	public Image getImage() {
		return image;
	}
}