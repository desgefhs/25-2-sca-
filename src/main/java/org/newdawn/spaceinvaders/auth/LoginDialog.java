package org.newdawn.spaceinvaders.auth;


import javax.swing.*;
import java.awt.*;
import javax.swing.WindowConstants;

/**
 * 사용자 인증을 위한 모달(modal) 대화 상자.
 * 이 다이얼로그는 {@link AuthScreen}을 내부에 포함하고,
 * UI에서 발생하는 액션(버튼 클릭 등)을 {@link AuthManager}의 로직과 연결하는 역할을 합니다.
 */
public class LoginDialog extends JDialog {

    /** 사용자 인증 UI를 담고 있는 패널. */
    private final AuthScreen authScreen;
    /** 인증 성공 시 생성되는 사용자 정보 객체. `transient`로 직렬화에서 제외됩니다. */
    private transient AuthenticatedUser authenticatedUser = null;

    /**
     * LoginDialog 생성자.
     * UI 컴포넌트를 설정하고 로그인 및 회원가입 버튼에 대한 액션 리스너를 추가합니다.
     *
     * @param owner 이 다이얼로그를 소유하는 부모 프레임
     * @param authManager 인증 로직을 처리할 AuthManager 인스턴스
     */
    public LoginDialog(Frame owner, AuthManager authManager) {
        super(owner, "Login", true); // `true`는 모달 대화 상자를 의미

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
                dispose(); // 로그인 성공 시 다이얼로그 닫기
            } else {
                authScreen.setMessage("Login failed. Check username/password.");
            }
        });

        setContentPane(authScreen);
        getRootPane().setDefaultButton(authScreen.getLoginButton());
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * 로그인 대화 상자를 화면에 표시하고, 사용자가 로그인하거나 창을 닫을 때까지 기다립니다.
     *
     * @return 로그인 성공 시 {@link AuthenticatedUser} 객체, 실패 또는 취소 시 null
     */
    public AuthenticatedUser showDialog() {
        setVisible(true);
        // setVisible(true)는 다이얼로그가 닫힐 때까지 이 지점에서 실행을 멈춥니다.
        return authenticatedUser;
    }
}