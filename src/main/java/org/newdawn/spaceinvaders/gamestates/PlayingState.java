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

/**
 * 게임 플레이 중의 상태를 나타내는 클래스.
 * 핵심 게임 로직과 렌더링, 입력 처리를 다른 컴포넌트에 위임하여 관리합니다.
 * 상태 진입 시 플레이어 함선 및 펫을 설정합니다.
 */
public class PlayingState implements GameState {

    private final GameContext gameContext;
    private final PlayingStateRenderer renderer; // 게임 플레이 화면 렌더링 담당
    private final PlayingInputHandler inputHandler; // 게임 플레이 중 입력 처리 담당

    /**
     * PlayingState 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public PlayingState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.renderer = new PlayingStateRenderer(gameContext);
        this.inputHandler = new PlayingInputHandler(gameContext);
    }

    /**
     * 이 상태에서는 특별한 초기화가 필요하지 않습니다.
     */
    @Override
    public void init() {
        // 이니셜 로직은 onEnter에서 호출되어 상태에 진입할 때마다 실행됩니다.
    }

    /**
     * 게임 플레이 중의 사용자 입력을 처리합니다.
     * @param input 현재 키 상태를 제공하는 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        inputHandler.handle(input);
    }

    /**
     * 게임 플레이 로직을 업데이트합니다.
     * @param delta 마지막 업데이트 이후 경과 시간
     */
    @Override
    public void update(long delta) {
        // 게임 루프 업데이트 순서에 대한 책임은 이제 GameManager에 있습니다.
        gameContext.updatePlayingLogic(delta);
    }

    /**
     * 게임 플레이 화면을 렌더링합니다.
     * @param g 그리기를 수행할 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        renderer.render(g);
    }

    /**
     * 이 상태에 진입할 때 호출됩니다.
     * 현재 게임 세션에 대한 플레이어 함선, 무기, 펫을 설정하고 웨이브 매니저를 초기화합니다.
     */
    @Override
    public void onEnter() {
        // 플레이어 함선, 무기, 펫을 이 게임 플레이 세션을 위해 설정합니다.
        setupPlayerShip();

        // 게임 플레이 상태에 진입할 때마다 웨이브 매니저의 타이머를 초기화합니다.
        gameContext.getGameContainer().getWaveManager().init();
    }

    /**
     * 플레이어 함선과 장착된 무기, 펫을 설정합니다.
     */
    private void setupPlayerShip() {
        PlayerData currentPlayer = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();
        String equippedWeaponName = currentPlayer.getEquippedWeapon();
        Weapon selectedWeapon;

        // 장착된 무기 이름에 따라 적절한 무기 객체 선택
        if (equippedWeaponName != null) {
            switch (equippedWeaponName) {
                case "Shotgun":
                    selectedWeapon = new Shotgun();
                    break;
                case "Laser":
                    selectedWeapon = new Laser();
                    break;
                default: // DefaultGun 또는 알 수 없는 무기
                    selectedWeapon = new DefaultGun();
                    break;
            }
        } else {
            selectedWeapon = new DefaultGun();
        }

        // 엔티티 매니저를 통해 함선을 초기화합니다.
        gameContext.getGameContainer().getEntityManager().initShip(gameContext.getGameContainer().getPlayerManager().getPlayerStats(), selectedWeapon);

        // 장착된 펫이 있으면 펫을 생성하고 게임에 추가합니다.
        if (currentPlayer.getEquippedPet() != null) {
            try {
                ShipEntity playerShip = gameContext.getShip();
                if (playerShip == null) {
                    System.err.println("펫 설정 전에 플레이어 함선이 초기화되지 않았습니다.");
                    return;
                }
                PetType petType = PetType.valueOf(currentPlayer.getEquippedPet());
                int petLevel = currentPlayer.getPetLevel(petType.name());

                PetEntity pet = PetFactory.createPet(petType, petLevel, gameContext, playerShip, playerShip.getX(), playerShip.getY());
                gameContext.addEntity(pet);

            } catch (IllegalArgumentException e) {
                System.err.println("알 수 없는 펫 타입으로 스폰을 시도했습니다: " + currentPlayer.getEquippedPet());
            }
        }
    }

    /**
     * 이 상태를 벗어날 때 특별한 로직이 필요하지 않습니다.
     */
    @Override
    public void onExit() {
        //사용하지 않음
    }
}
