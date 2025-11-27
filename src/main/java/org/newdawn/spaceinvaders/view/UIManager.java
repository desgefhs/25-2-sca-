package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.graphics.Sprite;

/**
 * 게임의 모든 UI 관련 뷰와 메뉴 객체들을 관리하는 컨테이너 클래스.
 * 다양한 UI 요소에 대한 중앙 접근점을 제공합니다.
 */
public class UIManager {

    /** 메인 게임 창. */
    private final GameWindow gameWindow;
    /** 메인 메뉴 뷰. */
    private final MainMenu mainMenu;
    /** 일시 정지 메뉴 뷰. */
    private final PauseMenu pauseMenu;
    /** 게임 오버 메뉴 뷰. */
    private final GameOverMenu gameOverMenu;
    /** 확인 대화 상자 뷰. */
    private final ConfirmDialog confirmDialog;
    /** 정적 배경 이미지 스프라이트. */
    private final Sprite staticBackgroundSprite;
    /** 상점 메뉴 뷰. 동적으로 생성되므로 setter를 통해 주입됩니다. */
    private ShopMenu shopMenu;

    /**
     * UIManager 생성자.
     * @param gameWindow 게임 창
     * @param mainMenu 메인 메뉴
     * @param pauseMenu 일시 정지 메뉴
     * @param gameOverMenu 게임 오버 메뉴
     * @param confirmDialog 확인 대화 상자
     * @param staticBackgroundSprite 정적 배경 스프라이트
     */
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

    /**
     * 상점 메뉴(ShopMenu)를 설정합니다.
     * 이 메뉴는 플레이어 데이터가 로드된 후 동적으로 생성되므로 별도의 setter가 필요합니다.
     * @param shopMenu 설정할 상점 메뉴 객체
     */
    public void setShopMenu(ShopMenu shopMenu) {
        this.shopMenu = shopMenu;
    }
}
