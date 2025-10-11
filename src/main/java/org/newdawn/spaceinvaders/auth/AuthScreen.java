package org.newdawn.spaceinvaders.auth;

import org.newdawn.spaceinvaders.core.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

// 로그인 및 회원가입 GUI 화면
public class AuthScreen extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    //오류 메세지
    private JLabel messageLabel;
    private Image backgroundImage;

    // AuthScreen 객체를 생성, GUI 초기화합니다
    public AuthScreen() {
        setPreferredSize(new Dimension(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT));
        try {
            URL url = this.getClass().getClassLoader().getResource("sprites/Login/LoginBackground.png");
            if (url != null) {
                backgroundImage = ImageIO.read(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);

        // 아이디 필드
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        usernameField.setOpaque(false);
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 90, 5, 10));

        try {
            URL idBoxUrl = this.getClass().getClassLoader().getResource("sprites/Login/IdBox.PNG");
            if (idBoxUrl != null) {
                Image idBoxImage = ImageIO.read(idBoxUrl);
                ImagePanel usernamePanel = new ImagePanel(idBoxImage);
                usernamePanel.setOpaque(false);
                usernamePanel.add(usernameField, BorderLayout.CENTER);
                add(usernamePanel, gbc);
            } else {
                add(usernameField, gbc);
            }
        } catch (IOException e) {
            e.printStackTrace();
            add(usernameField, gbc);
        }

        // 비밀번호 필드
        gbc.gridy = 1;
        passwordField = new JPasswordField(20);
        passwordField.setOpaque(false);
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 90, 5, 10));

        try {
            URL idBoxUrl = this.getClass().getClassLoader().getResource("sprites/Login/PwBox.PNG");
            if (idBoxUrl != null) {
                Image idBoxImage = ImageIO.read(idBoxUrl);
                ImagePanel passwordPanel = new ImagePanel(idBoxImage);
                passwordPanel.setOpaque(false);
                passwordPanel.add(passwordField, BorderLayout.CENTER);
                add(passwordPanel, gbc);
            } else {
                add(passwordField, gbc);
            }
        } catch (IOException e) {
            e.printStackTrace();
            add(passwordField, gbc);
        }

        // 로그인/회원가입 버튼
        gbc.gridy = 2;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        try {
            URL loginIconUrl = this.getClass().getClassLoader().getResource("sprites/Login/LoginButton.PNG");
            if (loginIconUrl != null) {
                ImageIcon originalIcon = new ImageIcon(loginIconUrl);
                Image originalImage = originalIcon.getImage();
                Image scaledImage = originalImage.getScaledInstance(120, 40, Image.SCALE_SMOOTH);
                loginButton = new JButton(new ImageIcon(scaledImage));
                loginButton.setBorderPainted(false);
                loginButton.setContentAreaFilled(false);
                loginButton.setFocusPainted(false);
            } else {
                loginButton = new JButton("Login");
            }
        } catch (Exception e) {
            loginButton = new JButton("Login");
            e.printStackTrace();
        }

        try {
            URL signupIconUrl = this.getClass().getClassLoader().getResource("sprites/Login/SignUpButton.PNG");
            if (signupIconUrl != null) {
                ImageIcon originalIcon = new ImageIcon(signupIconUrl);
                Image originalImage = originalIcon.getImage();
                Image scaledImage = originalImage.getScaledInstance(120, 40, Image.SCALE_SMOOTH);
                signupButton = new JButton(new ImageIcon(scaledImage));
                signupButton.setBorderPainted(false);
                signupButton.setContentAreaFilled(false);
                signupButton.setFocusPainted(false);
            } else {
                signupButton = new JButton("Sign Up");
            }
        } catch (Exception e) {
            signupButton = new JButton("Sign Up");
            e.printStackTrace();
        }

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        add(buttonPanel, gbc);

        // 오류 메세지 칸
        gbc.gridy = 3;
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);
    }

    // 배경 이미지 그리기
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getSignupButton() {
        return signupButton;
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    //배경 이미지 내부 클래스
    private class ImagePanel extends JPanel {
        private Image backgroundImage;

        public ImagePanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
            setLayout(new BorderLayout());
        }

        // 배경 이미지를 그리기
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}