package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어를 따라다니며 발사체를 발사하여 적을 공격하는 펫 엔티티.
 * 레벨에 따라 발사체 수가 증가합니다.
 */
public class AttackPetEntity extends PetEntity {

    /** 펫 능력의 기본 재사용 대기시간 (밀리초). */
    private static final long BASE_COOLDOWN = 2000; // 2초
    /** 공격형 펫의 스프라이트 리소스 경로. */
    private static final String ATTACK_PET_SPRITE = "sprites/pet/Attackpet.gif";

    /** 한 번에 발사되는 발사체 수. */
    private int projectileCount;

    /**
     * AttackPetEntity 생성자.
     * @param game 게임 컨텍스트
     * @param player 펫이 따라다닐 플레이어 함선
     * @param x 초기 x 위치
     * @param y 초기 y 위치
     * @param initialLevel 펫의 초기 레벨
     */
    public AttackPetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, ATTACK_PET_SPRITE, x, y, initialLevel);
        setScale(0.07);
    }

    /**
     * 펫의 현재 레벨에 따라 능력치(발사체 수)를 업데이트합니다.
     */
    @Override
    protected void updateStatsByLevel() {
        this.abilityCooldown = BASE_COOLDOWN; // 기본 재사용 대기시간
        this.projectileCount = 1; // 기본 발사체 수

        // 레벨에 따른 발사체 수 증가
        if (level >= 3) {
            this.projectileCount++;
        }
        if (level >= 6) {
            this.projectileCount++;
        }
        if (level >= 10) {
            this.projectileCount++;
        }
    }

    /**
     * 펫의 공격 능력을 활성화합니다.
     * 현재 `projectileCount`에 따라 여러 발의 발사체를 발사합니다.
     */
    @Override
    public void activateAbility() {
        int damage = 1; // 기본 피해량
        ProjectileType type = ProjectileType.PLAYER_SHOT; // 플레이어의 발사체 스프라이트 사용
        double moveSpeed = type.moveSpeed;

        for (int i = 0; i < this.projectileCount; i++) {
            int xOffset = (i - this.projectileCount / 2) * 15; // 다중 발사 시 중앙 기준 분산
            ProjectileEntity shot = new ProjectileEntity(game, type, damage, getX() + (getWidth() / 2) + xOffset, getY() - 30, 0, -moveSpeed);
            shot.setScale(0.8);
            game.addEntity(shot);
        }
    }
}