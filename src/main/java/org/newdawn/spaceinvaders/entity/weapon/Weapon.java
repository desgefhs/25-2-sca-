package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.player.PlayerStats;

/**
 * 게임 내 모든 무기가 구현해야 하는 인터페이스.
 * 무기의 발사, 업그레이드, 레벨 설정, 사운드 이름 제공 등
 * 무기의 공통적인 행동을 정의합니다.
 */
public interface Weapon {
    /**
     * 무기를 발사합니다.
     * @param context 게임 컨텍스트
     * @param owner 무기를 소유한 함선 엔티티
     */
    void fire(GameContext context, ShipEntity owner);

    /**
     * 무기를 업그레이드합니다.
     * 이 메소드를 구현하여 무기 자체의 업그레이드 로직을 정의할 수 있습니다.
     * @param stats 플레이어의 현재 스탯 (필요시 사용)
     */
    void upgrade(PlayerStats stats);

    /**
     * 무기의 레벨을 설정합니다.
     * 무기의 레벨에 따라 성능이 변화해야 합니다.
     * @param level 설정할 무기 레벨
     */
    void setLevel(int level);

    /**
     * 이 무기를 발사할 때 재생될 사운드의 이름을 반환합니다.
     * @return 사운드 이름
     */
    String getSoundName();
}
