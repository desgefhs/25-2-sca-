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

        GameContainer getGameContainer();

    

        // --- 게임플레이 액션 ---

        void startGameplay();

        void setCurrentState(GameState.Type stateType);

        void setNextState(GameState.Type stateType);

        void onWaveCleared();

        void updatePlayingLogic(long delta); // 추가됨

    

        // --- 엔티티 관리 ---

        void addEntity(Entity entity);

        void removeEntity(Entity entity);

        java.util.List<Entity> getEntities();

        ShipEntity getShip();

    

        // --- 플레이어 상태 ---

        boolean canPlayerAttack();

    

        // --- UI 및 렌더링 ---

        Background getBackground();

        Sprite getStaticBackgroundSprite();

        Map<String, Weapon> getWeapons();

        double getMoveSpeed();

        boolean getShowHitboxes();

        void setShowHitboxes(boolean show);

    

        // --- 알림 및 메시지 ---

        String getMessage();

        void setMessage(String message);

        void setMessageEndTime(long time);

    

        // --- 기타 ---

        boolean hasCollectedAllItems();

        void resetItemCollection();

                void setLogicRequiredThisLoop(boolean required);

        

                EventBus getEventBus();

            }