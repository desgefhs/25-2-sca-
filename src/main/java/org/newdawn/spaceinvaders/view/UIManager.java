package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.graphics.Sprite;

public class UIManager {

    private final GameWindow gameWindow;
    private final MainMenu mainMenu;
    private final PauseMenu pauseMenu;
    private final GameOverMenu gameOverMenu;
    private final ConfirmDialog confirmDialog;
    private final Sprite staticBackgroundSprite;
    // ShopMenu is created later, so it needs a setter.
    private ShopMenu shopMenu;

    public UIManager(GameWindow gameWindow, MainMenu mainMenu, PauseMenu pauseMenu, GameOverMenu gameOverMenu, ConfirmDialog confirmDialog, Sprite staticBackgroundSprite) {
        this.gameWindow = gameWindow;
        this.mainMenu = mainMenu;
        this.pauseMenu = pauseMenu;
        this.gameOverMenu = gameOverMenu;
        this.confirmDialog = confirmDialog;
        this.staticBackgroundSprite = staticBackgroundSprite;
    }

    // Getters
    public GameWindow getGameWindow() {
        return gameWindow;
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }

    public GameOverMenu getGameOverMenu() {
        return gameOverMenu;
    }

    public ConfirmDialog getConfirmDialog() {
        return confirmDialog;
    }

    public Sprite getStaticBackgroundSprite() {
        return staticBackgroundSprite;
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }

    // Setter for ShopMenu as it's created dynamically
    public void setShopMenu(ShopMenu shopMenu) {
        this.shopMenu = shopMenu;
    }
}
