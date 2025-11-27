package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.player.PlayerManager;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.sound.SoundManager;
import org.newdawn.spaceinvaders.wave.FormationManager;
import org.newdawn.spaceinvaders.wave.WaveManager;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.view.UIManager;

public class GameContainer {

    private final DatabaseManager databaseManager;
    private final PlayerManager playerManager;
    private final ShopManager shopManager;
    private final SoundManager soundManager;
    private final FormationManager formationManager;
    private final WaveManager waveManager;
    private final EntityManager entityManager;
    private final UIManager uiManager;
    private final GameStateManager gsm;
    private final InputHandler inputHandler;

    public GameContainer(DatabaseManager databaseManager, PlayerManager playerManager, ShopManager shopManager,
                         SoundManager soundManager, FormationManager formationManager, WaveManager waveManager,
                         EntityManager entityManager, UIManager uiManager, GameStateManager gsm, InputHandler inputHandler) {
        this.databaseManager = databaseManager;
        this.playerManager = playerManager;
        this.shopManager = shopManager;
        this.soundManager = soundManager;
        this.formationManager = formationManager;
        this.waveManager = waveManager;
        this.entityManager = entityManager;
        this.uiManager = uiManager;
        this.gsm = gsm;
        this.inputHandler = inputHandler;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public FormationManager getFormationManager() {
        return formationManager;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public UIManager getUiManager() {
        return uiManager;
    }

    public GameStateManager getGsm() {
        return gsm;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }
}
