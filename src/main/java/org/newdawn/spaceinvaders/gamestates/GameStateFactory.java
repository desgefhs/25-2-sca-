package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;

public class GameStateFactory {

    public GameState create(GameState.Type type, GameContext context) {
        return switch (type) {
            case MAIN_MENU -> new MainMenuState(context);
            case PLAYING -> new PlayingState(context);
            case PAUSED -> new PausedState(context);
            case GAME_OVER -> new GameOverState(context, false);
            case GAME_WON -> new GameOverState(context, true);
            case RANKING -> new RankingState(context);
            case SHOP -> new ShopState(context);
            case SHOP_MAIN_MENU -> new ShopMainMenuState(context);
            case ITEM_DRAW -> new ItemDrawState(context);
            case PET_MENU -> new PetMenuState(context);
            case WEAPON_MENU -> new WeaponMenuState(context);
            case EXIT_CONFIRMATION -> new ExitConfirmationState(context);
            default -> throw new IllegalArgumentException("Unknown game state type: " + type);
        };
    }
}
