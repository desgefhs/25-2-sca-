package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.boss.BossEntity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.*;

/**
 * 게임에 나타나는 모든 요소를 나타내는 추상 클래스
 */
public abstract class Entity {
	protected GameContext context;
	protected double x;
	protected double y;
	protected Sprite sprite;
	/** 엔티티의 현재 수평 속도 (pixels/sec) */
	protected double dx;
	/** 엔티티의 현재 수직 속도 (pixels/sec) */
	protected double dy;
	/** 이 엔티티의 충돌 감지를 위한 사각형 */
	private Rectangle me = new Rectangle();
	/** 다른 엔티티의 충돌 감지를 위한 사각형 */
	private Rectangle him = new Rectangle();

	/** 엔티티의 너비 */
	protected int width;
	/** 엔티티의 높이 */
	protected int height;
	/** 엔티티의 크기 배율 */
	protected double scale = 1.0;

	/** 엔티티의 체력 관련 로직을 처리하는 컴포넌트  */
	protected HealthComponent health;

	/**
	 * 스프라이트 이미지와 위치를 기반으로 엔티티를 생성
	 *
	 * @param ref 엔티티에 표시할 이미지의 참조 경로
 	 * @param x   초기 x 좌표
	 * @param y   초기 y 좌표
	 */
	public Entity(String ref,int x,int y) {
		this.sprite = SpriteStore.get().getSprite(ref);
		this.x = x;
		this.y = y;
		this.width = sprite.getWidth();
		this.height = sprite.getHeight();
	}

	/**
	 * 엔티티의 크기 배율을 설정
	 *
	 * @param scale 크기 배율
	 */
	public void setScale(double scale) {
		if (scale <= 0) return;
		this.scale = scale;
		this.width = (int) (sprite.getWidth() * scale);
		this.height = (int) (sprite.getHeight() * scale);
	}


	public HealthComponent getHealth() {
		return health;
	}

	/**
	 * 경과된 시간을 기반으로 엔티티를 이동
	 *
	 * @param delta 경과 시간 (밀리초)
	 */
	public void move(long delta) {
		// 이동 속도에 따라 위치 업데이트
		x += (delta * dx) / 1000;
		y += (delta * dy) / 1000;
	}

	/**
	 * 엔티티의 수평 속도를 설정
	 *
	 * @param dx 수평 속도 (pixels/sec)
	 */
	public void setHorizontalMovement(double dx) {
		this.dx = dx;
	}

	/**
	 * 엔티티의 수직 속도를 설정
	 * @param dy 수직 속도 (pixels/sec)
	 */
	public void setVerticalMovement(double dy) {
		this.dy = dy;
	}

	public double getHorizontalMovement() {
		return dx;
	}
	public double getVerticalMovement() {
		return dy;
	}

	/**
	 * 제공된 그래픽 컨텍스트에 이 엔티티를 그림
	 * 이동 방향에 따라 스프라이트를 회전
	 *
	 * @param g 그림을 그릴 그래픽 컨텍스트
	 */
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		java.awt.geom.AffineTransform oldTransform = g2d.getTransform();

		try {
			// 그래픽 컨텍스트를 변환하고 회전
			g2d.translate(x + width / 2.0, y + height / 2.0);

			// 엔티티가 실제로 움직일 때만 회전 (보스 제외)
			if ((dx != 0 || dy != 0) && !(this instanceof BossEntity)) {
				// 스프라이트가 일반적으로 '위쪽'(-Y)을 향해 그려지므로 PI/2를 더함
				double angle = Math.atan2(dy, dx) + Math.PI / 2;
				g2d.rotate(angle);
			}

			// 새로운 원점을 중심으로 스프라이트 그리기
			g2d.drawImage(sprite.getImage(), -width / 2, -height / 2, width, height, null);

		} finally {
			// 원래 변환으로 복원
			g2d.setTransform(oldTransform);
		}
	}

	/**
	 * 이 엔티티와 관련된 로직을 수행
	 * 이 메서드는 게임 이벤트에 따라 주기적으로 호출
	 */
	public void doLogic() {
	}

	public int getX() {
		return (int) x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public int getY() {
		return (int) y;
	}
	public int getHeight(){ return height; }

	/**
	 * 이 엔티티가 다른 엔티티와 충돌하는지 확인
	 *
	 * @param other 충돌을 확인할 다른 엔티티
	 * @return 충돌하면 true, 그렇지 않으면 false
	 */
	public boolean collidesWith(Entity other) {
		me.setBounds((int) x, (int) y, this.width, this.height);
		him.setBounds((int) other.x, (int) other.y, other.width, other.height);

		return me.intersects(him);
	}

	/**
	 * 이 엔티티가 다른 엔티티와 충돌했을 때 호출되는 알림 메서드
	 * 하위 클래스에서 반드시 구현
	 *
	 * @param other 충돌한 다른 엔티티
	 */
	public abstract void collidedWith(Entity other);

	/**
	 * 이 엔티티가 게임에서 제거되기 직전에 호출되는 알림 메서드
	 */
	public void onDestroy() {
	}
}