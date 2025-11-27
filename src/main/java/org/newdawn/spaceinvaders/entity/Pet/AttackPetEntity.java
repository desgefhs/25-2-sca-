package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 적을 공격하는 펫입니다.
 */
public class AttackPetEntity extends PetEntity {

    private static final long BASE_COOLDOWN = 2000; // 2초
    private static final String ATTACK_PET_SPRITE = "sprites/pet/Attackpet.gif";

    private int projectileCount;

    /**
     * 새로운 공격 펫을 생성합니다.
     *
     * @param game         펫이 존재하는 게임 컨텍스트
     * @param player       따라다닐 플레이어 함선
     * @param x            초기 x 위치
     * @param y            초기 y 위치
     * @param initialLevel 펫의 초기 레벨
     */
    public AttackPetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, ATTACK_PET_SPRITE, x, y, initialLevel);
        setScale(0.07);
    }

    @Override
    protected void updateStatsByLevel() {
        this.abilityCooldown = BASE_COOLDOWN; // 나중에 레벨에 따라 조정될 수 있습니다.
        this.projectileCount = 1;
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

    @Override
    public void activateAbility() {
        int damage = 1; // 기본 피해량, 나중에 업그레이드 가능

        ProjectileType type = ProjectileType.PLAYER_SHOT; // 플레이어의 발사체 스프라이트 사용
        double moveSpeed = type.moveSpeed;

        for (int i = 0; i < this.projectileCount; i++) {
            int xOffset = (i - this.projectileCount / 2) * 15;
            ProjectileEntity shot = new ProjectileEntity(game, type, damage, getX() + (getWidth() / 2) + xOffset, getY() - 30, 0, -moveSpeed);
            shot.setScale(0.8);
            game.addEntity(shot);
        }
    }
}