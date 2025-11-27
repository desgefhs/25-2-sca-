package org.newdawn.spaceinvaders.entity;

/**
 * 엔티티의 체력과 관련된 모든 로직을 관리하는 컴포넌트.
 * 데미지 처리, 생존 여부 확인, 체력 초기화를 담당합니다.
 */
public class HealthComponent {

    /** 이 컴포넌트를 소유한 엔티티. */
    private final Entity owner;
    /** 실제 체력 데이터를 담고 있는 객체. */
    private final HP hp;

    /**
     * HealthComponent 생성자.
     * @param owner 이 컴포넌트의 소유자 엔티티
     * @param maxHp 최대 체력
     */
    public HealthComponent(Entity owner, int maxHp) {
        this.owner = owner;
        this.hp = new HP(maxHp, maxHp);
    }

    /**
     * 엔티티의 체력을 감소시킵니다.
     * 소유자가 무적 함선인 경우 피해를 무시합니다.
     * 플레이어의 경우, 데미지를 입은 후 짧은 무적 시간을 부여합니다.
     *
     * @param amount 감소시킬 체력의 양
     * @return 데미지를 입은 후에도 살아있으면 true, 죽었으면 false
     */
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

    /**
     * 체력을 최대치로 리셋합니다.
     */
    //체력 초기화
    public void reset() {
        hp.setCurrentHp(hp.getMAX_HP());
    }

    /**
     * 체력을 최대치까지 완전히 회복합니다.
     */
    public void fullyHeal() {
        hp.setCurrentHp(hp.getMAX_HP());
    }

    /**
     * 내부 HP 데이터 객체를 반환합니다.
     * @return HP 객체
     */
    public HP getHp() {
        return hp;
    }

    /**
     * 엔티티가 살아있는지 확인합니다.
     * @return 현재 체력이 0보다 크면 true
     */
    //생존 여부 확인
    public boolean isAlive() {
        return hp.getCurrentHp() > 0;
    }

    /**
     * 엔티티의 현재 체력을 정수형으로 반환합니다.
     * @return 현재 체력
     */
    public int getCurrentHealth() {
        return (int) hp.getCurrentHp();
    }

    /**
     * 엔티티의 체력을 특정 양만큼 증가시킵니다. 최대 체력을 초과하지 않습니다.
     * @param amount 증가시킬 체력의 양
     */
    public void increaseHealth(int amount) {
        hp.setCurrentHp(Math.min(hp.getMAX_HP(), hp.getCurrentHp() + amount));
    }

    /**
     * 엔티티의 최대 체력을 반환합니다.
     * @return 최대 체력
     */
    public int getMaxHp() {
        return (int) hp.getMAX_HP();
    }
}