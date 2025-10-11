package org.newdawn.spaceinvaders.graphics;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * 게임에 사용되는 모든 스프라이트(이미지)를 관리하는 저장소 클래스
 * 싱글톤 패턴을 사용하여 유일한 인스턴스를 제공하며, 한 번 로드한 스프라이트는 캐싱하여 재사용
 */
public class SpriteStore {
	/** SpriteStore의 유일한 인스턴스 */
	private static SpriteStore single = new SpriteStore();

	/**
	 * SpriteStore의 싱글톤 인스턴스를 반환
	 *
	 * @return SpriteStore 인스턴스
	 */
	public static SpriteStore get() {
		return single;
	}

	/** 로드된 스프라이트를 캐싱하기 위한 맵 (경로, Sprite 객체) */
	private HashMap sprites = new HashMap();

	/**
	 * 지정된 참조 경로에 해당하는 스프라이트를 반환
	 * 만약 스프라이트가 이미 캐시에 존재하면 캐시된 객체를 반환하고,
	 * 그렇지 않으면 이미지 파일을 로드하여 새로운 Sprite 객체를 생성하고 캐시에 저장한 후 반환
	 *
	 * @param ref 스프라이트의 리소스 경로 (예: "sprites/ship.gif")
	 * @return 요청된 Sprite 객체
	 */
	public Sprite getSprite(String ref) {
		// 캐시에 이미 스프라이트가 있는지 확인
		if (sprites.get(ref) != null) {
			return (Sprite) sprites.get(ref);
		}

		BufferedImage sourceImage = null;
		
		try {
			URL url = this.getClass().getClassLoader().getResource(ref);
			
			if (url == null) {
				// 보스 사이클 이미지 로드 실패 시 기본 이미지로 대체 시도
				if (ref.contains("_cycle")) {
					String fallbackRef = ref.replaceAll("_cycle[0-9]+", "_cycle0");
					url = this.getClass().getClassLoader().getResource(fallbackRef);
				}

				if (url == null) {
					fail("참조를 찾을 수 없습니다: " + ref);
				}
			}

			// 이미지 파일 읽기
			sourceImage = ImageIO.read(url);
		} catch (IOException e) {
			fail("로드 실패: "+ref);
		}

		// 현재 그래픽 환경에 맞는 호환 이미지 생성
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);

		// 호환 이미지에 원본 이미지 그리기
		image.getGraphics().drawImage(sourceImage,0,0,null);

		// 생성된 이미지로 Sprite 객체 생성 및 캐시에 저장
		Sprite sprite = new Sprite(image);
		sprites.put(ref,sprite);
		
		return sprite;
	}

	/**
	 * 심각한 오류 발생 시 메시지를 출력하고 프로그램을 종료
	 *
	 * @param message 오류 메시지
	 */
	private void fail(String message) {
		System.err.println(message);
		System.exit(0);
	}
}