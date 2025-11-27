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
 * 스프라이트(이미지)를 로드하고 캐싱하는 싱글턴(Singleton) 클래스.
 * 한 번 로드된 스프라이트는 메모리에 저장하여 반복적인 파일 I/O를 방지합니다.
 */
public class SpriteStore {

	/** 이 클래스의 유일한 인스턴스. */
	private static final SpriteStore single = new SpriteStore();

	/**
	 * SpriteStore의 싱글턴 인스턴스를 반환합니다.
	 * @return SpriteStore 인스턴스
	 */
	public static SpriteStore get() {
		return single;
	}

	/** 로드된 스프라이트를 캐싱하는 맵. (Key: 리소스 경로, Value: Sprite 객체) */
	private final HashMap<String, Sprite> sprites = new HashMap<>();

	/**
	 * 지정된 리소스 경로에서 스프라이트를 가져옵니다.
	 * 만약 스프라이트가 이미 캐시되어 있다면, 캐시된 인스턴스를 반환합니다.
	 * 그렇지 않으면, 리소스로부터 이미지를 로드하고, 호환되는 이미지로 변환한 후,
	 * 새로운 Sprite 객체를 생성하여 캐시에 저장하고 반환합니다.
	 *
	 * @param ref 스프라이트 이미지 파일의 리소스 경로 (예: "sprites/ship.gif")
	 * @return 요청된 Sprite 객체
	 */
	public Sprite getSprite(String ref) {
		// 캐시에 이미 존재하는지 확인
		if (sprites.get(ref) != null) {
			return sprites.get(ref);
		}

		BufferedImage sourceImage = null;

		try {
			URL url = this.getClass().getClassLoader().getResource(ref);

			if (url == null) {
				// 순환 애니메이션(_cycle)에 대한 폴백(fallback) 로직
				if (ref.contains("_cycle")) {
					String fallbackRef = ref.replaceAll("_cycle[0-9]+", "_cycle0");
					url = this.getClass().getClassLoader().getResource(fallbackRef);
				}

				if (url == null) {
					fail("리소스를 찾을 수 없습니다: " + ref);
				}
			}
			// URL에서 이미지 로드
			sourceImage = ImageIO.read(url);
		} catch (IOException e) {
			fail("리소스 로드 실패: "+ref);
		}

		// 현재 그래픽 환경에 맞는 호환 이미지 생성 (성능 최적화)
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);

		// 호환 이미지에 원본 이미지 그리기
		image.getGraphics().drawImage(sourceImage,0,0,null);

		// 새로운 Sprite 객체 생성 및 캐시에 저장
		Sprite sprite = new Sprite(image);
		sprites.put(ref,sprite);

		return sprite;
	}

	/**
	 * 심각한 오류가 발생했을 때 메시지를 출력하고 프로그램을 종료합니다.
	 * @param message 출력할 오류 메시지
	 */
	private void fail(String message) {
		System.err.println(message);
		System.exit(0);
	}
}