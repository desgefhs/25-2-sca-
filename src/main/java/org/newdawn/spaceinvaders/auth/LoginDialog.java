package org.newdawn.spaceinvaders.auth;


import javax.swing.*;
import java.awt.*;

/**
 * 로그인 및 회원가입 표시하는 클래스
 */
public class LoginDialog extends JDialog {

    // 로그인 및 회원가입 화면
    private final AuthScreen authScreen;
    //** 인증된 사용자 정보
    private AuthenticatedUser authenticatedUser = null;

    //  authManager 사용자 인증을 처리
    public LoginDialog(Frame owner, AuthManager authManager) {
        super(owner, "Login", true); //

        authScreen = new AuthScreen();

        // 회원가입 버튼 액션 리스너
        authScreen.getSignupButton().addActionListener(e -> {
            String username = authScreen.getUsername();
            String password = authScreen.getPassword();
            if (username.isEmpty() || password.isEmpty()) {
                authScreen.setMessage("Username and password cannot be empty.");
                return;
            }
            if (password.length() < 6) {
                authScreen.setMessage("Password must be at least 6 characters.");
                return;
            }
            boolean success = authManager.signUp(username, password);
            if (success) {
                authScreen.setMessage("Sign up successful! Please log in.");
            } else {
                authScreen.setMessage("Sign up failed. Username may already exist.");
            }
        });

        // 로그인 버튼 액션 리스너
        authScreen.getLoginButton().addActionListener(e -> {
            String username = authScreen.getUsername();
            String password = authScreen.getPassword();
            if (username.isEmpty() || password.isEmpty()) {
                authScreen.setMessage("Username and password cannot be empty.");
                return;
            }
            String docId = authManager.signIn(username, password);
            if (docId != null) {
                this.authenticatedUser = new AuthenticatedUser(docId, username);
                dispose(); // 로그인 성공시 닫기
            } else {
                authScreen.setMessage("Login failed. Check username/password.");
            }
        });

        setContentPane(authScreen);
        getRootPane().setDefaultButton(authScreen.getLoginButton());
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }


    public AuthenticatedUser showDialog() {
        setVisible(true);

        return authenticatedUser;
    }
}
