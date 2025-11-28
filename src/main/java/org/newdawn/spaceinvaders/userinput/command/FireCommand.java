package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어의 함선이 무기를 발사하도록 시도하는 커맨드.
 */
public class FireCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;

    /**
     * FireCommand 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public FireCommand(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * 플레이어의 함선 엔티티를 찾아 {@link ShipEntity#tryToFire()} 메소드를 호출하여 발사를 시도합니다.
     */
    @Override
    public void execute() {
        // 실행 시점에 함선 객체 가져오기
        ShipEntity ship = gameContext.getShip();
        if (ship != null) {
            ship.tryToFire();
        }
    }
}