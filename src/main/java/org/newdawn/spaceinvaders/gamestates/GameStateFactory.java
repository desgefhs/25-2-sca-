package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;

/**
 * {@link GameState.Type}에 따라 적절한 {@link GameState} 객체를 생성하는 팩토리 클래스.
 * 게임의 상태 생성 로직을 캡슐화합니다.
 */
public class GameStateFactory {

    /**
     * 주어진 {@link GameState.Type}에 해당하는 {@link GameState} 인스턴스를 생성합니다.
     *
     * @param type 생성할 게임 상태의 타입
     * @param context 게임 컨텍스트
     * @return 생성된 {@link GameState} 객체
     * @throws IllegalArgumentException 알 수 없는 게임 상태 타입이 주어질 경우
     */
    public GameState create(GameState.Type type, GameContext context) {
        return switch (type) {
            case MAIN_MENU -> new MainMenuState(context);
            case PLAYING -> new PlayingState(context);
            case PAUSED -> new PausedState(context);
            case GAME_OVER -> new GameOverState(context, false); // 패배 상태
            case GAME_WON -> new GameOverState(context, true);    // 승리 상태
            case RANKING -> new RankingState(context);
            case SHOP -> new ShopState(context);
            case SHOP_MAIN_MENU -> new ShopMainMenuState(context);
            case ITEM_DRAW -> new ItemDrawState(context);
            case PET_MENU -> new PetMenuState(context);
            case WEAPON_MENU -> new WeaponMenuState(context);
            case EXIT_CONFIRMATION -> new ExitConfirmationState(context);
            default -> throw new IllegalArgumentException("알 수 없는 게임 상태 타입: " + type);
        };
    }
}
