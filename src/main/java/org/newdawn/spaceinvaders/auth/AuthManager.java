package org.newdawn.spaceinvaders.auth;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AuthManager {

    private final Firestore db;
    private final CollectionReference usersCollection;

    public AuthManager(Firestore db) {
        this.db = db;
        this.usersCollection = db.collection("users");
    }

    public boolean signUp(String username, String password) {
        try {
            // Check if username already exists
            ApiFuture<QuerySnapshot> future = usersCollection.whereEqualTo("username", username).get();
            QuerySnapshot snapshot = future.get();
            if (!snapshot.isEmpty()) {
                System.err.println("Username already exists.");
                return false;
            }

            // Hash the password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            System.out.println("DEBUG: Hashed Password to be saved: " + hashedPassword);

            // Create a new user document
            Map<String, Object> user = new HashMap<>();
            user.put("username", username);
            user.put("hashedPassword", hashedPassword);

            usersCollection.add(user).get();
            return true;

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String signIn(String username, String password) {
        try {
            // Find the user by username
            ApiFuture<QuerySnapshot> future = usersCollection.whereEqualTo("username", username).limit(1).get();
            QuerySnapshot snapshot = future.get();

            if (snapshot.isEmpty()) {
                System.err.println("User not found.");
                return null;
            }

            // Get the user document
            DocumentSnapshot userDoc = snapshot.getDocuments().get(0);
            String hashedPasswordFromDB = userDoc.getString("hashedPassword");

            // Check the password
            if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
                return userDoc.getId(); // Return the document ID as the localId
            } else {
                System.err.println("Incorrect password.");
                return null;
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
