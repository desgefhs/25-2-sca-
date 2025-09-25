package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.AuthManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 게임 스타일의 커스텀 로그인 팝업창.
 */
public class LoginDialog extends JDialog {

    private AuthManager authManager;
    private JTextField emailField;
    private JPasswordField passwordField;
    private boolean loginSuccess = false;

    public LoginDialog(Frame owner, AuthManager authManager) {
        super(owner, "Login", true); // true for modal
        this.authManager = authManager;

        // --- 창 기본 설정 ---
        setUndecorated(true); // OS 창 테두리 제거
        setSize(400, 300);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(Color.BLACK);
        setLayout(null); // 절대 위치 지정을 위해 null 레이아웃 사용

        // --- UI 컴포넌트 생성 및 스타일링 ---
        JLabel titleLabel = new JLabel("LOGIN");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        titleLabel.setBounds(160, 30, 100, 30);

        JLabel idLabel = new JLabel("ID:");
        idLabel.setForeground(Color.WHITE);
        idLabel.setBounds(50, 90, 80, 25);

        emailField = new JTextField();
        styleTextField(emailField);
        emailField.setBounds(140, 90, 200, 25);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(50, 130, 80, 25);

        passwordField = new JPasswordField();
        styleTextField(passwordField);
        passwordField.setBounds(140, 130, 200, 25);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.setBounds(80, 200, 100, 30);

        JButton registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.setBounds(220, 200, 100, 30);

        // --- 컴포넌트 추가 ---
        add(titleLabel);
        add(idLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(registerButton);

        // --- 이벤트 리스너 설정 ---
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = emailField.getText();
                String password = new String(passwordField.getPassword());
                String email = username + "@game.com"; // 입력된 ID에 @game.com 자동 추가

                if (authManager.login(email, password)) {
                    loginSuccess = true;
                    dispose(); // 로그인 성공 시 창 닫기
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "Login Failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = emailField.getText();
                String password = new String(passwordField.getPassword());
                String email = username + "@game.com"; // 입력된 ID에 @game.com 자동 추가

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginDialog.this, "ID and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (authManager.register(email, password)) {
                    JOptionPane.showMessageDialog(LoginDialog.this, "Registration successful! You can now log in.");
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "Registration failed. The ID might already be in use.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void styleTextField(JTextField field) {
        field.setBackground(Color.DARK_GRAY);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    }

    private void styleButton(JButton button) {
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    }

    /**
     * 로그인 성공 여부를 반환합니다.
     * @return 로그인이 성공했으면 true
     */
    public boolean isLoginSuccessful() {
        return loginSuccess;
    }
}
