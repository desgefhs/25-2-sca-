package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.player.PlayerStats;

/**
 * 게임 내 모든 무기가 구현해야 하는 인터페이스
 * 무기의 공통적인 동작(발사, 업그레이드 등)을 정의
 */
public interface Weapon {
    /**
     * 무기를 발사
     *
     * @param context 발사된 발사체를 게임에 추가하기 위한 게임 컨텍스트
     * @param owner   무기를 발사하는 우주선 엔티티
     */
    void fire(GameContext context, ShipEntity owner);

    /**
     * 플레이어의 능력치에 따라 무기를 업그레이드
     *
     * @param stats 플레이어 능력치
     */
    void upgrade(PlayerStats stats);

    /**
     * 무기의 레벨을 설정
     *
     * @param level 설정할 레벨
     */
    void setLevel(int level);

    /**
     * 무기 발사 시 재생할 사운드의 이름을 반환
     *
     * @return 사운드 이름
     */
    String getSoundName();
}
