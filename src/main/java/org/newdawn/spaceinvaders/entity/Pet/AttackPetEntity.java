package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 주기적으로 적을 공격하는 펫 엔티티
 * 펫의 레벨에 따라 발사하는 발사체 수가 증가
 */
public class AttackPetEntity extends PetEntity {

    /** 공격 재사용 대기시간 (2초) */
    private static final long ATTACK_COOLDOWN = 2000;
    /** 공격 펫 스프라이트 경로 */
    private static final String ATTACK_PET_SPRITE = "sprites/pet/Attackpet.gif";

    /**
     * 새로운 공격 펫을 생성
     *
     * @param game   펫이 존재할 게임 컨텍스트
     * @param player 따라다닐 플레이어 우주선
     * @param x      초기 x 좌표
     * @param y      초기 y 좌표
     */
    public AttackPetEntity(GameContext game, ShipEntity player, int x, int y) {
        super(game, player, ATTACK_PET_SPRITE, x, y, ATTACK_COOLDOWN);
        setScale(0.07);
    }

    /**
     * 펫의 공격 능력을 활성화
     * 펫의 레벨에 따라 여러 개의 발사체를 발사
     */
    @Override
    public void activateAbility() {
        GameManager gm = (GameManager) game;
        int level = gm.getCurrentPlayer().getPetLevel(PetType.ATTACK.name());

        // 레벨에 따라 발사체 수 결정
        int projectileCount = 1;
        if (level >= 3) {
            projectileCount++;
        }
        if (level >= 6) {
            projectileCount++;
        }
        if (level >= 10) {
            projectileCount++;
        }

        int damage = 1; // 기본 데미지

        ProjectileType type = ProjectileType.PLAYER_SHOT; // 플레이어 발사체 스프라이트 사용
        double moveSpeed = type.moveSpeed;

        // 결정된 수만큼 발사체 생성
        for (int i=0; i < projectileCount; i++) {
            int xOffset = (i - projectileCount / 2) * 15;
            ProjectileEntity shot = new ProjectileEntity(game, type, damage, getX() + (getWidth()/2) + xOffset, getY() - 30, 0, -moveSpeed);
            shot.setScale(0.8);
            game.addEntity(shot);
        }
    }
}
