package org.newdawn.spaceinvaders.entity;

/**
 * 엔티티의 체력과 관련된 모든 로직을 관리하는 컴포넌트.
 * 데미지 처리, 생존 여부 확인, 체력 초기화 .
 */
public class HealthComponent {

    private final Entity owner;
    private final HP hp;

    public HealthComponent(Entity owner, int maxHp) {
        this.owner = owner;
        this.hp = new HP(maxHp, maxHp);
    }

    // 데미지 처리
    public boolean decreaseHealth(double amount) {
        // 소유자가 무적 함선인 경우 피해를 무시합니다.
        if (owner instanceof ShipEntity && ((ShipEntity) owner).isInvincible()) {
            return true; // 아직 살아있고 피해를 입지 않았습니다.
        }

        hp.setCurrentHp(hp.getCurrentHp() - amount);

        if (hp.getCurrentHp() <= 0) {
            return false; // 죽음
        }

        // 살아있고 플레이어인 경우 일시적인 무적 상태를 부여합니다.
        if (owner instanceof ShipEntity) {
            ((ShipEntity) owner).activateInvincibility();
        }

        return true; // 살아있음
    }

    //체력 초기화
    public void reset() {
        hp.setCurrentHp(hp.getMAX_HP());
    }

    public void fullyHeal() {
        hp.setCurrentHp(hp.getMAX_HP());
    }

    public HP getHp() {
        return hp;
    }

    //생존 여부 확인
    public boolean isAlive() {
        return hp.getCurrentHp() > 0;
    }

    public int getCurrentHealth() {
        return (int) hp.getCurrentHp();
    }

    public void increaseHealth(int amount) {
        hp.setCurrentHp(Math.min(hp.getMAX_HP(), hp.getCurrentHp() + amount));
    }

    public int getMaxHp() {
        return (int) hp.getMAX_HP();
    }
}