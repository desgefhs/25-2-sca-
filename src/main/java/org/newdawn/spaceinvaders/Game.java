package org.newdawn.spaceinvaders;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.newdawn.spaceinvaders.view.LoginDialog;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 게임의 주 진입점(Entry Point) 역할을 하는 클래스.
 */
public class Game {

    //게임 시작
    public static void main(String[] argv) {
        // Initialize Firestore
        Firestore db = initializeFirebase();
        if (db == null) {
            System.err.println("Failed to initialize Firebase. Exiting.");
            return;
        }

        // Create AuthManager
        AuthManager authManager = new AuthManager(db);

        // Create and show the login dialog
        // A null frame is used since the main game frame doesn't exist yet.
        LoginDialog loginDialog = new LoginDialog(null, authManager);
        AuthenticatedUser user = loginDialog.showDialog();

        // If the user is null, it means they closed the dialog without logging in.
        if (user == null) {
            System.out.println("Login cancelled. Exiting.");
            System.exit(0);
        }

        // If login is successful, create and start the game
        GameManager gameManager = new GameManager(user, db);
        gameManager.startGame();
    }

    private static Firestore initializeFirebase() {
        try {
            // Use getResourceAsStream to load the key from the classpath
            // This is more portable than using a file path
            InputStream serviceAccount = Game.class.getClassLoader().getResourceAsStream("serviceAccountKey.json");
            if (serviceAccount == null) {
                throw new IOException("serviceAccountKey.json not found in classpath");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            return FirestoreClient.getFirestore();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
