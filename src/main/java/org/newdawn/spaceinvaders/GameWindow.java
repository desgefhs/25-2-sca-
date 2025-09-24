package org.newdawn.spaceinvaders;

import org.newdawn.spaceinvaders.entity.Entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.List;

/**
 * 게임 창(JFrame)을 생성하고, 화면에 모든 그래픽 요소를 그리는 책임을 가지는 클래스.
 */
public class GameWindow extends Canvas {

    private final JFrame container;
    private final BufferStrategy strategy;

    public GameWindow(InputHandler inputHandler) {
        // 메인 창(프레임) 생성
        container = new JFrame("Space Invaders");

        // 프레임의 컨텐츠 패널 설정
        JPanel panel = (JPanel) container.getContentPane();
        panel.setPreferredSize(new Dimension(800, 600));
        panel.setLayout(null);

        // 캔버스(이 클래스 자신)의 크기를 설정하고 패널에 추가
        setBounds(0, 0, 800, 600);
        panel.add(this);

        // AWT가 캔버스를 다시 그리지 않도록 설정 (직접 그릴 것이기 때문)
        setIgnoreRepaint(true);

        // 창 보이기
        container.pack();
        container.setResizable(false);
        container.setVisible(true);

        // 창 닫기 이벤트 처리
        container.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // 키 입력 핸들러 추가
        addKeyListener(inputHandler);
        // 포커스 요청
        requestFocus();

        // 더블 버퍼링 전략 생성
        createBufferStrategy(2);
        strategy = getBufferStrategy();
    }

    /**
     * 화면에 모든 엔티티와 메시지를 그립니다.
     * @param entities 그릴 엔티티 목록
     * @param message 화면에 표시할 메시지
     */
    public void render(List<Entity> entities, String message) {
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 800, 600);

        // 모든 엔티티 그리기
        for (Entity entity : entities) {
            entity.draw(g);
        }

        // 메시지가 있으면 그리기
        if (message != null && !message.isEmpty()) {
            g.setColor(Color.white);
            g.drawString(message, (800 - g.getFontMetrics().stringWidth(message)) / 2, 250);
            g.drawString("Press any key", (800 - g.getFontMetrics().stringWidth("Press any key")) / 2, 300);
        }

        g.dispose();
        strategy.show();
    }

    public void setTitle(String title) {
        container.setTitle(title);
    }
}
