package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Pet.PetEntity;
import org.newdawn.spaceinvaders.entity.Pet.PetFactory;
import org.newdawn.spaceinvaders.entity.Pet.PetType;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.weapon.DefaultGun;
import org.newdawn.spaceinvaders.entity.weapon.Laser;
import org.newdawn.spaceinvaders.entity.weapon.Shotgun;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.userinput.PlayingInputHandler;
import org.newdawn.spaceinvaders.view.PlayingStateRenderer;

import java.awt.Graphics2D;

public class PlayingState implements GameState {

    private final GameContext gameContext;
    private final PlayingStateRenderer renderer;
    private final PlayingInputHandler inputHandler;

    public PlayingState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.renderer = new PlayingStateRenderer(gameContext);
        this.inputHandler = new PlayingInputHandler(gameContext);
    }

    @Override
    public void init() {
        // The init logic is now part of onEnter to ensure it's called every time we enter the state
    }

    @Override
    public void handleInput(InputHandler input) {
        // All input handling is now delegated to the dedicated handler class.
        inputHandler.handle(input);
    }

    @Override
    public void update(long delta) {
        // The responsibility for the game loop's update order now belongs to GameManager.
        gameContext.updatePlayingLogic(delta);
    }

    @Override
    public void render(Graphics2D g) {
        renderer.render(g);
    }

    @Override
    public void onEnter() {
        // Initialize the player's ship, weapon, and pet for this gameplay session.
        setupPlayerShip();

        // Initialize the wave manager's timers every time we enter the playing state
        gameContext.getGameContainer().getWaveManager().init();
    }

    private void setupPlayerShip() {
        PlayerData currentPlayer = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();
        String equippedWeaponName = currentPlayer.getEquippedWeapon();
        Weapon selectedWeapon;
        if (equippedWeaponName != null) {
            switch (equippedWeaponName) {
                case "Shotgun":
                    selectedWeapon = new Shotgun();
                    break;
                case "Laser":
                    selectedWeapon = new Laser();
                    break;
                default:
                    selectedWeapon = new DefaultGun();
                    break;
            }
        } else {
            selectedWeapon = new DefaultGun();
        }

        gameContext.getGameContainer().getEntityManager().initShip(gameContext.getGameContainer().getPlayerManager().getPlayerStats(), selectedWeapon);

        if (currentPlayer.getEquippedPet() != null) {
            try {
                ShipEntity playerShip = gameContext.getShip();
                // Ensure ship is not null before proceeding
                if (playerShip == null) {
                    System.err.println("Player ship not initialized before pet setup.");
                    return;
                }
                PetType petType = PetType.valueOf(currentPlayer.getEquippedPet());
                int petLevel = currentPlayer.getPetLevel(petType.name());

                PetEntity pet = PetFactory.createPet(petType, petLevel, gameContext, playerShip, playerShip.getX(), playerShip.getY());
                gameContext.addEntity(pet);

            } catch (IllegalArgumentException e) {
                System.err.println("Attempted to spawn unknown pet type: " + currentPlayer.getEquippedPet());
            }
        }
    }

    @Override
    public void onExit() {}
}
