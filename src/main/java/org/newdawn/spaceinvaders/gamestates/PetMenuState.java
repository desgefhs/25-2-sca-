package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Pet.PetType;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;
import org.newdawn.spaceinvaders.userinput.PetMenuInputHandler;
import org.newdawn.spaceinvaders.view.PetMenuView;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetMenuState implements GameState {

    private final GameContext gameContext;
    private PlayerData playerData;
    private PetMenuView petMenuView;
    private PetMenuInputHandler inputHandler;
    private final Map<String, Sprite> petSprites = new HashMap<>();

    public PetMenuState(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void init() {
        petSprites.put("ATTACK", SpriteStore.get().getSprite("sprites/pet/Attackpet.gif"));
        petSprites.put("DEFENSE", SpriteStore.get().getSprite("sprites/pet/Defensepet.gif"));
        petSprites.put("HEAL", SpriteStore.get().getSprite("sprites/pet/Healpet.gif"));
        petSprites.put("BUFF", SpriteStore.get().getSprite("sprites/pet/Buffpet.gif"));
    }

    private void setupMenu() {
        List<String> ownedPetNames = new ArrayList<>();
        if (playerData != null) {
            ownedPetNames = new ArrayList<>(playerData.getPetInventory().keySet());
            ownedPetNames.sort(String::compareTo);
        }
        this.petMenuView = new PetMenuView(ownedPetNames);
        this.inputHandler = new PetMenuInputHandler(gameContext, () -> this.petMenuView);
    }

    @Override
    public void handleInput(InputHandler input) {
        if (inputHandler != null) {
            inputHandler.handle(input);
        }
    }

    @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        if (petMenuView != null) {
            petMenuView.render(g, playerData, petSprites, gameContext.getMessage());
        }
    }

    @Override
    public void onEnter() {
        this.playerData = gameContext.getPlayerManager().getCurrentPlayer();
        setupMenu();
        gameContext.setMessage("");
    }

    @Override
    public void onExit() {}
}
