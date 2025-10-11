package org.newdawn.spaceinvaders.entity;

/**
 * 엔티티의 체력과 관련된 모든 로직을 관리하는 컴포넌트.
 * 데미지 처리, 생존 여부 확인, 체력 초기화 .
 */
public class HealthComponent {

    private final Entity owner;
    private HP hp;

    public HealthComponent(Entity owner, int maxHp) {
        this.owner = owner;
        this.hp = new HP(maxHp, maxHp);
    }

    // 데미지 처리
    public boolean decreaseHealth(double amount) {
        // If the owner is an invincible ship, ignore damage
        if (owner instanceof ShipEntity && ((ShipEntity) owner).isInvincible()) {
            return true; // Still alive, no damage taken
        }

        hp.setCurrentHp(hp.getCurrentHp() - amount);

        if (hp.getCurrentHp() <= 0) {
            return false; // Dead
        }

        // If alive and it's the player, grant temporary invincibility
        if (owner instanceof ShipEntity) {
            ((ShipEntity) owner).activateInvincibility();
        }

        return true; // Alive
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
