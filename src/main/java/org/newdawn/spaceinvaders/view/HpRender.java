package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.HP;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * HP 데이터(HP.java)를 기반으로 화면에 체력 바를 그리는 책임을 가지는 클래스.
 */
public class HpRender {

    private final HP hp;

    private final int Hprendersize = 30; // HP 바 크기 조정

    public HpRender(HP hp) {
        this.hp = hp;
    }

    // Entity를 직접 받아와서 그 위치에 HP 바를 렌더링하도록 수정
    public void hpRender(Graphics2D g, Entity entity) {
        double hpY = entity.getY() - 10; // 엔티티의 y좌표 바로 위에 표시
        double hpX = entity.getX();

        g.setColor(new Color(70, 70, 70));
        g.fill(new Rectangle2D.Double(hpX, hpY, Hprendersize, 4)); // HP 바 배경
        g.setColor(new Color(253, 91, 91));
        // HP 계산 버그 수정
        double hpsize = (hp.getCurrentHp() / hp.getMAX_HP()) * Hprendersize;
        g.fill(new Rectangle2D.Double(hpX, hpY, hpsize, 4)); // 현재 HP
    }
}