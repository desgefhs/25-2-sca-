package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * 게임에 나타나는 모든 요소(플레이어, 적, 발사체 등)를 나타내는 추상 기본 클래스.
 * 엔티티는 위치, 크기, 속도와 같은 기본 속성을 가지며, 이동, 렌더링, 충돌 감지 등의
 * 공통적인 동작을 정의합니다.
 * <p>
 * 위치에 double을 사용하는 이유는, 정수 픽셀 사이의 부분적인 이동을 허용하여
 * 프레임 간 이동 계산에서 정밀도를 잃지 않기 위함입니다.
 *
 * @author Kevin Glass (original)
 */
public abstract class Entity {
	/** 이 엔티티가 속한 게임 컨텍스트. */
	protected GameContext context;
	/** 이 엔티티의 현재 x 위치. */
	protected double x;
	/** 이 엔티티의 현재 y 위치. */
	protected double y;
	/** 이 엔티티를 나타내는 스프라이트. */
	protected Sprite sprite;
	/** 이 엔티티의 현재 수평 속도 (픽셀/초). */
	protected double dx;
	/** 이 엔티티의 현재 수직 속도 (픽셀/초). */
	protected double dy;
	/** 이 엔티티의 너비. */
	protected int width;
	/** 이 엔티티의 높이. */
	protected int height;
	/** 스프라이트 크기 배율. */
	protected double scale = 1.0;
	/** 엔티티의 체력 관련 로직을 처리하는 컴포넌트 (선택적). */
	protected HealthComponent health;

	/** 충돌 감지를 위해 이 엔티티의 경계를 나타내는 사각형. */
	private final Rectangle me = new Rectangle();
	/** 충돌 감지를 위해 다른 엔티티의 경계를 나타내는 사각형. */
	private final Rectangle him = new Rectangle();

	/** 엔티티가 파괴되었는지 여부. */
	private boolean destroyed = false;

	/**
	 * 스프라이트 이미지와 위치를 기반으로 엔티티를 생성합니다.
	 *
	 * @param ref 이 엔티티에 표시될 이미지에 대한 참조
 	 * @param x 이 엔티티의 초기 x 위치
	 * @param y 이 엔티티의 초기 y 위치
	 */
	public Entity(String ref, int x, int y) {
		this.sprite = SpriteStore.get().getSprite(ref);
		this.x = x;
		this.y = y;
		this.width = sprite.getWidth();
		this.height = sprite.getHeight();
	}

	/**
	 * 엔티티의 크기 배율을 설정합니다.
	 * @param scale 설정할 배율 값 (0보다 커야 함)
	 */
	public void setScale(double scale) {
		if (scale <= 0) return;
		this.scale = scale;
		this.width = (int) (sprite.getWidth() * scale);
		this.height = (int) (sprite.getHeight() * scale);
	}

	/**
	 * 엔티티의 HealthComponent를 반환합니다.
	 * @return HealthComponent 인스턴스, 없으면 null.
	 */
	public HealthComponent getHealth() {
		return health;
	}

	/**
	 * 엔티티의 상태를 리셋합니다. (예: `destroyed` 플래그 초기화)
	 */
	public void reset() {
		this.destroyed = false;
	}

	/**
	 * 지정된 시간(delta) 동안 엔티티를 이동시킵니다.
	 * @param delta 경과된 시간 (밀리초)
	 */
	public void move(long delta) {
		x += (delta * dx) / 1000.0;
		y += (delta * dy) / 1000.0;
	}

	/**
	 * 이 엔티티의 수평 속도를 설정합니다.
	 * @param dx 수평 속도 (픽셀/초)
	 */
	public void setHorizontalMovement(double dx) {
		this.dx = dx;
	}

	/**
	 * 이 엔티티의 수직 속도를 설정합니다.
	 * @param dy 수직 속도 (픽셀/초)
	 */
	public void setVerticalMovement(double dy) {
		this.dy = dy;
	}

	/**
	 * 이 엔티티의 수평 속도를 가져옵니다.
	 * @return 수평 속도 (픽셀/초)
	 */
	public double getHorizontalMovement() {
		return dx;
	}

	/**
	 * 이 엔티티의 수직 속도를 가져옵니다.
	 * @return 수직 속도 (픽셀/초)
	 */
	public double getVerticalMovement() {
		return dy;
	}

	/**
	 * 제공된 그래픽 컨텍스트에 이 엔티티를 그립니다.
	 * 이동 방향에 따라 스프라이트를 회전시키는 로직을 포함합니다.
	 *
	 * @param g 그리기를 수행할 그래픽 컨텍스트
	 */
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform oldTransform = g2d.getTransform();

		try {
			// 그래픽 컨텍스트를 엔티티의 중심으로 이동
			g2d.translate(x + width / 2.0, y + height / 2.0);

			// 이동 중인 경우에만 이동 방향으로 회전 (보스 제외)
			if ((dx != 0 || dy != 0) && !(this instanceof BossEntity)) {
				// 스프라이트가 기본적으로 위쪽(음수 Y)을 향하므로 PI/2를 더함
				double angle = Math.atan2(dy, dx) + Math.PI / 2;
				g2d.rotate(angle);
			}

			// 새로운 원점을 중심으로 스프라이트를 그림
			g2d.drawImage(sprite.getImage(), -width / 2, -height / 2, width, height, null);

		} finally {
			// 다음 렌더링을 위해 그래픽 컨텍스트의 변환을 원래대로 복원
			g2d.setTransform(oldTransform);
		}
	}

	/**
	 * 이 엔티티와 관련된 주기적인 로직을 수행합니다.
	 * (예: 발사 시도, 패턴 변경 등)
	 */
	public void doLogic() {
	}

	/**
	 * 이 엔티티의 x 위치를 가져옵니다.
	 * @return 정수형 x 좌표
	 */
	public int getX() {
		return (int) x;
	}

	/**
	 * 이 엔티티의 y 위치를 가져옵니다.
	 * @return 정수형 y 좌표
	 */
	public int getY() {
		return (int) y;
	}

	/**
	 * 이 엔티티의 x 위치를 설정합니다.
	 * @param x 새로운 x 좌표
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * 이 엔티티의 y 위치를 설정합니다.
	 * @param y 새로운 y 좌표
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * 이 엔티티의 너비를 가져옵니다.
	 * @return 너비 (픽셀)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 이 엔티티의 높이를 가져옵니다.
	 * @return 높이 (픽셀)
	 */
	public int getHeight(){
		return height;
	}

	/**
	 * 이 엔티티가 다른 엔티티와 충돌했는지 확인합니다.
	 *
	 * @param other 충돌을 확인할 다른 엔티티
	 * @return 엔티티들이 서로 충돌하면 true
	 */
	public boolean collidesWith(Entity other) {
		me.setBounds((int) x, (int) y, this.width, this.height);
		him.setBounds((int) other.x, (int) other.y, other.width, other.height);
		return me.intersects(him);
	}

	/**
	 * 다른 엔티티와 충돌했을 때 호출되는 추상 메소드.
	 * 하위 클래스에서 충돌 시의 구체적인 동작을 구현해야 합니다.
	 *
	 * @param other 이 엔티티와 충돌한 다른 엔티티
	 */
	public abstract void collidedWith(Entity other);

	/**
	 * 이 엔티티가 게임에서 제거되기 직전에 호출됩니다.
	 * 하위 클래스는 이를 재정의하여 정리 작업을 수행할 수 있습니다. (예: 폭발 효과 생성)
	 */
	public void onDestroy() {
	}

	/**
	 * 이 엔티티를 파괴 상태로 표시합니다.
	 * 다음 생명주기 처리에서 게임 월드에서 제거됩니다.
	 */
	public void destroy() {
		this.destroyed = true;
	}

	/**
	 * 이 엔티티가 파괴 상태인지 확인합니다.
	 * @return 파괴 상태이면 true
	 */
	public boolean isDestroyed() {
		return destroyed;
	}
}