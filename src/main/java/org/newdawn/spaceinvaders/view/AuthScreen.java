
package org.newdawn.spaceinvaders.view;

import javax.swing.*;
import java.awt.*;

public class AuthScreen extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel messageLabel;

    public AuthScreen() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);

        // Username Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        // Username Field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        add(usernameField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        // Password Field
        gbc.gridx = 1;
        gbc.gridy = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        add(buttonPanel, gbc);

        // Message Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);
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
}
