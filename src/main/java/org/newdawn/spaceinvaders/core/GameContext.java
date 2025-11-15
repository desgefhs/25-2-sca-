package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.core.GameStateManager;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.player.PlayerManager;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.sound.SoundManager;
import org.newdawn.spaceinvaders.view.*;
import org.newdawn.spaceinvaders.wave.WaveManager;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;

import java.util.Map;

public interface GameContext {

    // --- Core Sub-Systems ---
    EntityManager getEntityManager();
    PlayerManager getPlayerManager();
    WaveManager getWaveManager();
    DatabaseManager getDatabaseManager();
    ShopManager getShopManager();
    SoundManager getSoundManager();
    GameStateManager getGsm();

    // --- Gameplay Actions ---
    void startGameplay();
    void saveGameResults();
    void savePlayerData();
    void setCurrentState(GameState.Type stateType);
    void onWaveCleared();
    void updatePlayingLogic(long delta); // ADDED

    // --- Entity Management ---
    void addEntity(Entity entity);
    void removeEntity(Entity entity);
    java.util.List<Entity> getEntities();
    ShipEntity getShip();

    // --- Player Status ---
    boolean canPlayerAttack();

    // --- UI & Rendering ---
    Background getBackground();
    Sprite getStaticBackgroundSprite();
    MainMenu getMainMenu();
    PauseMenu getPauseMenu();
    GameOverMenu getGameOverMenu();
    ConfirmDialog getConfirmDialog();
    ShopMenu getShopMenu();
    Map<String, Weapon> getWeapons();
    double getMoveSpeed();
    boolean getShowHitboxes();
    void setShowHitboxes(boolean show);

    // --- Notifications & Messages ---
    void notifyDeath();
    void notifyWin();
    void notifyAlienEscaped(Entity entity);
    void notifyAlienKilled();
    void notifyMeteorDestroyed(int scoreValue);
    void notifyItemCollected();
    String getMessage();
    void setMessage(String message);
    void setMessageEndTime(long time);

    // --- Misc ---
    boolean hasCollectedAllItems();
    void resetItemCollection();
    void setLogicRequiredThisLoop(boolean required);
}