package org.newdawn.spaceinvaders.auth;

import org.newdawn.spaceinvaders.core.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * 사용자 인증(로그인 및 회원가입)을 위한 UI를 제공하는 Swing JPanel.
 * 사용자 이름, 비밀번호 필드, 로그인/회원가입 버튼, 메시지 레이블로 구성됩니다.
 */
public class AuthScreen extends JPanel {

    /** 사용자 이름 입력을 위한 텍스트 필드. */
    private final JTextField usernameField;
    /** 비밀번호 입력을 위한 패스워드 필드. */
    private final JPasswordField passwordField;
    /** 로그인 실행 버튼. */
    private JButton loginButton;
    /** 회원가입 실행 버튼. */
    private JButton signupButton;
    /** 오류나 상태 메시지를 표시하기 위한 레이블. */
    private final JLabel messageLabel;
    /** 패널의 배경 이미지. `transient`로 직렬화에서 제외됩니다. */
    private transient Image backgroundImage;

    /**
     * AuthScreen 생성자.
     * UI 컴포넌트를 초기화하고 레이아웃을 설정합니다.
     */
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
        usernameField.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 90, 5, 10)); // 약간의 패딩 추가

        try {
            URL idBoxUrl = this.getClass().getClassLoader().getResource("sprites/Login/IdBox.PNG");
            if (idBoxUrl != null) {
                Image idBoxImage = ImageIO.read(idBoxUrl);
                ImagePanel usernamePanel = new ImagePanel(idBoxImage);
                usernamePanel.setOpaque(false);
                usernamePanel.add(usernameField, BorderLayout.CENTER);
                add(usernamePanel, gbc);
            } else {
                add(usernameField, gbc); // 텍스트 필드로 대체
            }
        } catch (IOException e) {
            e.printStackTrace();
            add(usernameField, gbc); // 오류 발생 시 대체
        }

        // 비밀번호 필드
        gbc.gridy = 1;
        passwordField = new JPasswordField(20);
        passwordField.setOpaque(false);
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 90, 5, 10)); // 약간의 패딩 추가

        try {
            URL idBoxUrl = this.getClass().getClassLoader().getResource("sprites/Login/PwBox.PNG");
            if (idBoxUrl != null) {
                Image idBoxImage = ImageIO.read(idBoxUrl);
                ImagePanel passwordPanel = new ImagePanel(idBoxImage);
                passwordPanel.setOpaque(false);
                passwordPanel.add(passwordField, BorderLayout.CENTER);
                add(passwordPanel, gbc);
            } else {
                add(passwordField, gbc); // 텍스트 필드로 대체
            }
        } catch (IOException e) {
            e.printStackTrace();
            add(passwordField, gbc); // 오류 발생 시 대체
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

    /**
     * 컴포넌트의 배경을 그립니다. 배경 이미지가 있으면 이미지를 먼저 그립니다.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * 사용자가 입력한 사용자 이름을 반환합니다.
     * @return 공백이 제거된 사용자 이름
     */
    public String getUsername() {
        return usernameField.getText().trim();
    }

    /**
     * 사용자가 입력한 비밀번호를 반환합니다.
     * @return 공백이 제거된 비밀번호
     */
    public String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }

    /**
     * 로그인 버튼 객체를 반환합니다.
     * @return 로그인 버튼
     */
    public JButton getLoginButton() {
        return loginButton;
    }

    /**
     * 회원가입 버튼 객체를 반환합니다.
     * @return 회원가입 버튼
     */
    public JButton getSignupButton() {
        return signupButton;
    }

    /**
     * 메시지 레이블에 표시할 텍스트를 설정합니다.
     * @param message 표시할 메시지
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * 사용자 이름 필드 객체를 반환합니다.
     * @return 사용자 이름 텍스트 필드
     */
    public JTextField getUsernameField() {
        return usernameField;
    }

    /**
     * 배경 이미지를 그릴 수 있는 커스텀 JPanel.
     */
    private class ImagePanel extends JPanel {
        /** 패널의 배경 이미지. */
        private final transient Image backgroundImage;

        /**
         * ImagePanel 생성자.
         * @param backgroundImage 패널의 배경으로 사용할 이미지
         */
        public ImagePanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}