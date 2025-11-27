package org.newdawn.spaceinvaders.graphics;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.HP;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * 엔티티(플레이어, 보스 등)의 체력(HP) 바를 화면에 그리는 역할을 하는 클래스.
 */
public class HpRender {

    /** 체력 정보를 가지고 있는 HP 객체. */
    private final HP hp;

    /** 화면에 그려질 HP 바의 너비. */
    private final int Hprendersize = 30;

    /**
     * HpRender 생성자.
     * @param hp 체력 정보를 담고 있는 HP 객체
     */
    public HpRender(HP hp) {
        this.hp = hp;
    }

    /**
     * 대상 엔티티의 위치를 기준으로 HP 바를 그립니다.
     * @param g 그래픽 컨텍스트
     * @param entity HP 바를 표시할 대상 엔티티
     */
    public void hpRender(Graphics2D g, Entity entity) {
        // HP 바를 엔티티의 상단 중앙에 위치시킴
        double hpY = entity.getY() - 10;
        double hpX = entity.getX() + (entity.getWidth() / 2) - (Hprendersize / 2); // Reverted 2.0 to 2

        // HP 바 배경 그리기
        g.setColor(new Color(70, 70, 70));
        g.fill(new Rectangle2D.Double(hpX, hpY, Hprendersize, 4));

        // 현재 HP 그리기
        g.setColor(new Color(253, 91, 91));
        double hpsize = (hp.getCurrentHp() / hp.getMAX_HP()) * Hprendersize;
        g.fill(new Rectangle2D.Double(hpX, hpY, hpsize, 4));
    }
}