
package org.newdawn.spaceinvaders.auth;

public class AuthenticatedUser {
    private final String localId;
    private final String username;

    public AuthenticatedUser(String localId, String username) {
        this.localId = localId;
        this.username = username;
    }

    public String getLocalId() {
        return localId;
    }

    public String getUsername() {
        return username;
    }
}
