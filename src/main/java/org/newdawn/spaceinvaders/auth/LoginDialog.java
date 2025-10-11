package org.newdawn.spaceinvaders.auth;


import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private final AuthScreen authScreen;
    private AuthenticatedUser authenticatedUser = null;

    public LoginDialog(Frame owner, AuthManager authManager) {
        super(owner, "Login", true); //

        authScreen = new AuthScreen();

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
                dispose(); // Close the dialog on successful login
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
