package org.newdawn.spaceinvaders.graphics;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.HP;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * 엔티티의 체력(HP) 바를 화면에 그리는 역할을 하는 클래스
 */
public class HpRender {

    /** 체력 정보를 담고 있는 HP 객체 */
    private final HP hp;

    /** HP 바의 너비 */
    private final int Hprendersize = 30;

    /**
     * HpRender 객체를 생성
     *
     * @param hp 렌더링할 체력 정보
     */
    public HpRender(HP hp) {
        this.hp = hp;
    }

    /**
     * 지정된 엔티티의 위치 위에 체력 바를 렌더링
     *
     * @param g      그래픽 컨텍스트
     * @param entity 체력 바를 표시할 대상 엔티티
     */
    public void hpRender(Graphics2D g, Entity entity) {
        // 엔티티의 중앙 상단에 위치하도록 좌표 계산
        double hpY = entity.getY() - 10;
        double hpX = entity.getX() + (entity.getWidth() / 2.0) - (Hprendersize / 2.0);

        // HP 바 배경 그리기
        g.setColor(new Color(70, 70, 70));
        g.fill(new Rectangle2D.Double(hpX, hpY, Hprendersize, 4));

        // 현재 HP 비율에 맞춰 HP 바 그리기
        g.setColor(new Color(253, 91, 91));
        double hpsize = (hp.getCurrentHp() / hp.getMAX_HP()) * Hprendersize;
        g.fill(new Rectangle2D.Double(hpX, hpY, hpsize, 4));
    }
}