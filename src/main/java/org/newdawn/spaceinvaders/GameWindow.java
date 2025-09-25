package org.newdawn.spaceinvaders;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.view.MainMenu;
import org.newdawn.spaceinvaders.GameState;
import org.newdawn.spaceinvaders.SpriteStore;
import org.newdawn.spaceinvaders.Sprite;

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

    private final Sprite backgroundSprite; // 배경화면용 스프라이트

    public GameWindow(InputHandler inputHandler) {

        // 배경 이미지를 미리 불러온다.
        this.backgroundSprite = SpriteStore.get().getSprite("sprites/background.jpg");

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
     * 게임 플레이 화면을 그립니다.
     * @param entities 그릴 엔티티 목록
     * @param message 화면 중앙에 표시할 메시지
     * @param score 현재 점수
     * @param currentState 현재 게임 상태
     * @param backgroundY 배경 스크롤 y좌표
     */
    public void render(List<Entity> entities, String message, int score, GameState currentState, double backgroundY, int wave) {
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 800, 600);

        // 모든 엔티티 그리기
        for (Entity entity : entities) {
            entity.draw(g);
        }

        // 점수 그리기
        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 14));
        g.drawString(String.format("Score: %03d", score), 680, 30);
        g.drawString(String.format("Wave: %d", wave), 20, 30);

        // 게임오버/승리 메시지가 있으면 그리기
        if (message != null && !message.isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(message, (800 - g.getFontMetrics().stringWidth(message)) / 2, 250);
            if (currentState == GameState.GAME_OVER || currentState == GameState.GAME_WON) {
                g.drawString("Press Enter to Continue", (800 - g.getFontMetrics().stringWidth("Press Enter to Continue")) / 2, 300);
            }
        }

        g.dispose();
        strategy.show();
    }

    public void setTitle(String title) {
        container.setTitle(title);
    }

    /**
     * 메인 메뉴를 화면에 그립니다.
     * @param menu 그릴 메뉴 객체
     */
    public void renderMenu(MainMenu menu) {
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
        // 배경 이미지 그리기 (화면 전체에 맞게)
        g.drawImage(backgroundSprite.getImage(), 0, 0, 800, 600, null);

        // 메뉴 아이템들을 화면 하단에 가로로 배열하여 그리기
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 40;

        for (int i = 0; i < menu.getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(menu.getItem(i));
        }
        totalWidth += (menu.getItemCount() - 1) * spacing;

        int currentX = (800 - totalWidth) / 2;

        for (int i = 0; i < menu.getItemCount(); i++) {
            if (i == menu.getSelectedIndex()) {
                g.setColor(Color.GREEN); // 선택된 아이템
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(menu.getItem(i), currentX, 500);
            currentX += g.getFontMetrics().stringWidth(menu.getItem(i)) + spacing;
        }

        g.dispose();
        strategy.show();
    }
}
