package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어 함선의 이동 벡터를 수정하는 커맨드.
 * 함선의 현재 이동량에 주어진 값을 더합니다.
 */
public class MoveCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 수평 이동량 (x축). */
    private final double dx;
    /** 수직 이동량 (y축). */
    private final double dy;

    /**
     * MoveCommand 생성자.
     * @param gameContext 게임 컨텍스트
     * @param dx 수평 이동량
     * @param dy 수직 이동량
     */
    public MoveCommand(GameContext gameContext, double dx, double dy) {
        this.gameContext = gameContext;
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * 플레이어 함선의 현재 이동량에 이 커맨드의 dx, dy 값을 더합니다.
     * 이 커맨드가 제대로 작동하려면, 매 입력 처리 주기 시작 시 함선의 이동량이 0으로 리셋되어야 합니다.
     */
    @Override
    public void execute() {
        // 커맨드 생성 시점이 아닌 실행 시점에 함선 객체를 가져옴
        ShipEntity ship = gameContext.getShip();
        if (ship == null) {
            return;
        }
        ship.setHorizontalMovement(ship.getHorizontalMovement() + dx);
        ship.setVerticalMovement(ship.getVerticalMovement() + dy);
    }
}