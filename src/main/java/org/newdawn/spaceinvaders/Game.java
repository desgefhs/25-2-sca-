package org.newdawn.spaceinvaders;


import javax.swing.*;
import org.newdawn.spaceinvaders.view.LoginDialog;

/**
 * 게임의 진입 역할을 하는 클래스.
 */
public class Game {

    public Game() {
        // 인증 관리자 생성 및 초기화
        AuthManager authManager = new AuthManager();
        authManager.initialize();

        // 로그인 로그 생성
        JFrame tempFrame = new JFrame();
        tempFrame.setUndecorated(true);
        tempFrame.pack();
        tempFrame.setLocationRelativeTo(null);

        LoginDialog loginDialog = new LoginDialog(tempFrame, authManager);
        loginDialog.setVisible(true); //

        // 로그인 성공 여부 확인
        if (loginDialog.isLoginSuccessful()) {
            // 로그인 성공 시
            tempFrame.dispose();
            GameManager gameManager = new GameManager();
            gameManager.startGame();
        } else {
            // 로그인 실패 시
            System.exit(0);
        }
    }

    //게임 시작
    public static void main(String[] argv) {
        new Game();
    }
}