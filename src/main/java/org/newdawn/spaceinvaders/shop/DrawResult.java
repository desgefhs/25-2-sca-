package org.newdawn.spaceinvaders.shop;

public class DrawResult {
    private final String message;
    private final boolean success;

    public DrawResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
