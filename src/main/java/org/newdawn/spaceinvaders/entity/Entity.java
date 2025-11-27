package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.*;


/**
 * 엔티티는 게임에 나타나는 모든 요소를 나타냅니다.
 * 엔티티는 하위 클래스나 외부에서 정의된 속성 집합을 기반으로
 * 충돌 및 이동을 해결할 책임이 있습니다.
 *
 * 위치에 double을 사용하는 점에 유의하세요. 픽셀 위치가 정수라는 점을 감안하면
 * 이상하게 보일 수 있습니다. 하지만 double을 사용하면 엔티티가 부분적인
 * 픽셀을 이동할 수 있습니다. 물론 픽셀 중간에 표시된다는 의미는 아니지만,
 * 이동할 때 정확도를 잃지 않도록 해줍니다.
 *
 * @author Kevin Glass
 */
public abstract class Entity {
	/** 이 엔티티가 속한 게임 컨텍스트 */
	protected GameContext context;
	/** 이 엔티티의 현재 x 위치 */
	protected double x;
	/** 이 엔티티의 현재 y 위치 */
	protected double y;
	/** 이 엔티티를 나타내는 스프라이트 */
	protected Sprite sprite;
	/** 이 엔티티의 현재 수평 속도 (픽셀/초) */
	protected double dx;
	/** 이 엔티티의 현재 수직 속도 (픽셀/초) */
	protected double dy;
	/** 충돌 해결 중 이 엔티티에 사용되는 사각형 */
	private final Rectangle me = new Rectangle();
	/** 충돌 해결 중 다른 엔티티에 사용되는 사각형 */
	private final Rectangle him = new Rectangle();

	protected int width;
	protected int height;
	protected double scale = 1.0;

	/** 엔티티가 파괴될 수 있는 경우, 체력 관련 로직을 처리 (선택적) */
	protected HealthComponent health;
	
	/**
	 * 스프라이트 이미지와 위치를 기반으로 엔티티를 생성합니다.
	 *
	 * @param ref 이 엔티티에 표시될 이미지에 대한 참조
 	 * @param x 이 엔티티의 초기 x 위치
	 * @param y 이 엔티티의 초기 y 위치
	 */
	public Entity(String ref,int x,int y) {
		this.sprite = SpriteStore.get().getSprite(ref);
		this.x = x;
		this.y = y;
		this.width = sprite.getWidth();
		this.height = sprite.getHeight();
	}

	public void setScale(double scale) {
		if (scale <= 0) return;
		this.scale = scale;
		this.width = (int) (sprite.getWidth() * scale);
		this.height = (int) (sprite.getHeight() * scale);
	}

	public HealthComponent getHealth() {
		return health;
	}

	public void reset() {
		this.destroyed = false;
	}
	
	/**
	 * 특정 시간이 지남에 따라 이 엔티티가 스스로 이동하도록 요청합니다.
	 *
	 * @param delta 경과된 시간 (밀리초)
	 */
	public void move(long delta) {
		// 이동 속도에 따라 엔티티의 위치를 업데이트합니다.
		x += (delta * dx) / 1000;
		y += (delta * dy) / 1000;
	}
	
	/**
	 * 이 엔티티의 수평 속도를 설정합니다.
	 *
	 * @param dx 이 엔티티의 수평 속도 (픽셀/초)
	 */
	public void setHorizontalMovement(double dx) {
		this.dx = dx;
	}

	/**
	 * 이 엔티티의 수직 속도를 설정합니다.
	 *
	 */
	public void setVerticalMovement(double dy) {
		this.dy = dy;
	}
	
	/**
	 * 이 엔티티의 수평 속도를 가져옵니다.
	 *
	 * @return 이 엔티티의 수평 속도 (픽셀/초)
	 */
	public double getHorizontalMovement() {
		return dx;
	}

	/**
	 * 이 엔티티의 수직 속도를 가져옵니다.
	 *
	 * @return 이 엔티티의 수직 속도 (픽셀/초)
	 */
	public double getVerticalMovement() {
		return dy;
	}
	
	/**
	 * 제공된 그래픽 컨텍스트에 이 엔티티를 그립니다.
	 *
	 * @param g 그릴 그래픽 컨텍스트
	 */
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		java.awt.geom.AffineTransform oldTransform = g2d.getTransform();

		try {
			// 그래픽 컨텍스트를 변환하고 회전합니다.
			g2d.translate(x + width / 2.0, y + height / 2.0);

			// 엔티티가 실제로 움직이는 경우에만 회전합니다.
			if ((dx != 0 || dy != 0) && !(this instanceof BossEntity)) {
				// 스프라이트가 일반적으로 "위쪽"(음수 Y)을 향하도록 그려지므로 PI/2를 더합니다.
				double angle = Math.atan2(dy, dx) + Math.PI / 2;
				g2d.rotate(angle);
			}

			// 새 원점을 중심으로 스프라이트를 그립니다.
			g2d.drawImage(sprite.getImage(), -width / 2, -height / 2, width, height, null);

		} finally {
			// 원래 변환을 복원합니다.
			g2d.setTransform(oldTransform);
		}
	}
	
	/**
	 * 이 엔티티와 관련된 로직을 수행합니다. 이 메소드는
	 * 게임 이벤트에 따라 주기적으로 호출됩니다.
	 */
	public void doLogic() {
	}
	
	/**
	 * 이 엔티티의 x 위치를 가져옵니다.
	 *
	 * @return 이 엔티티의 x 위치
	 */
	    public int getX() {
	        return (int) x;
	    }

	    public void setX(double x) {
		    this.x = x;
		}
	
		public void setY(double y) {
		    this.y = y;
		}
	
		    public int getWidth() {	        return width;
	    }
	/**
	 * 이 엔티티의 y 위치를 가져옵니다.
	 *
	 * @return 이 엔티티의 y 위치
	 */
	public int getY() {
		return (int) y;
	}

	public int getHeight(){ return height; }
	
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
	 * 이 엔티티가 다른 엔티티와 충돌했다는 알림입니다.
	 *
	 * @param other 이 엔티티와 충돌한 엔티티.
	 */
	    public abstract void collidedWith(Entity other);
	
	    /**
	     * 이 엔티티가 게임에서 제거될 것이라는 알림입니다.
	     * 하위 클래스는 이를 재정의하여 정리 작업을 수행할 수 있습니다.
	     */
	    public void onDestroy() {
	    }

	    private boolean destroyed = false;

	    public void destroy() {
	        this.destroyed = true;
	    }

	    public boolean isDestroyed() {
	        return destroyed;
	    }
	}