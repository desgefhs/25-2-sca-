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

/**
 * 펫 관리 메뉴를 담당하는 게임 상태.
 * 플레이어가 소유한 펫을 확인하고, 펫을 장착하거나 강화하는 등의 상호작용을 처리합니다.
 */
public class PetMenuState implements GameState {

    private final GameContext gameContext;
    private PlayerData playerData; // 현재 플레이어 데이터
    private PetMenuView petMenuView; // 펫 메뉴를 그리는 뷰
    private PetMenuInputHandler inputHandler; // 펫 메뉴 입력 핸들러
    private final Map<String, Sprite> petSprites = new HashMap<>(); // 펫 타입별 스프라이트

    /**
     * PetMenuState 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public PetMenuState(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * 펫 메뉴 상태를 초기화합니다.
     * 펫 스프라이트를 미리 로드합니다.
     */
    @Override
    public void init() {
        petSprites.put("ATTACK", SpriteStore.get().getSprite("sprites/pet/Attackpet.gif"));
        petSprites.put("DEFENSE", SpriteStore.get().getSprite("sprites/pet/Defensepet.gif"));
        petSprites.put("HEAL", SpriteStore.get().getSprite("sprites/pet/Healpet.gif"));
        petSprites.put("BUFF", SpriteStore.get().getSprite("sprites/pet/Buffpet.gif"));
    }

    /**
     * 현재 플레이어 데이터를 기반으로 펫 메뉴를 설정합니다.
     * 플레이어가 소유한 펫 목록을 가져와 메뉴 뷰를 초기화합니다.
     */
    private void setupMenu() {
        List<String> ownedPetNames = new ArrayList<>();
        if (playerData != null) {
            ownedPetNames = new ArrayList<>(playerData.getPetInventory().keySet());
            ownedPetNames.sort(String::compareTo); // 펫 이름을 알파벳 순으로 정렬
        }
        this.petMenuView = new PetMenuView(ownedPetNames);
        // 펫 메뉴 뷰의 공급자를 람다식으로 제공
        this.inputHandler = new PetMenuInputHandler(gameContext, () -> this.petMenuView);
    }

    /**
     * 펫 메뉴에 대한 사용자 입력을 처리합니다.
     * @param input 현재 키 상태를 제공하는 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        if (inputHandler != null) {
            inputHandler.handle(input);
        }
    }

    /**
     * 이 상태에서는 특별한 업데이트 로직이 필요하지 않습니다.
     * @param delta 마지막 업데이트 이후 경과 시간
     */
    @Override
    public void update(long delta) {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 펫 메뉴 화면을 렌더링합니다.
     * @param g 그리기를 수행할 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        if (petMenuView != null) {
            petMenuView.render(g, playerData, petSprites, gameContext.getMessage());
        }
    }

    /**
     * 이 상태에 진입할 때 호출됩니다.
     * 최신 플레이어 데이터를 로드하고 메뉴를 설정합니다. 메시지도 초기화합니다.
     */
    @Override
    public void onEnter() {
        this.playerData = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();
        setupMenu();
        gameContext.setMessage(""); // 이전 상태의 메시지를 지웁니다.
    }

    /**
     * 이 상태를 벗어날 때 특별한 로직이 필요하지 않습니다.
     */
    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}
