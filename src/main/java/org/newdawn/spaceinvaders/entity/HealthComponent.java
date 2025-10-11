package org.newdawn.spaceinvaders.entity;

/**
 * 엔티티의 체력과 관련된 모든 로직을 관리하는 컴포넌트
 * 데미지 처리, 생존 여부 확인, 체력 초기화 등의 기능을 제공
 */
public class HealthComponent {

    /** 이 컴포넌트를 소유한 엔티티 */
    private final Entity owner;
    /** 체력(HP) 객체 */
    private HP hp;

    /**
     * HealthComponent 객체를 생성
     *
     * @param owner 소유자 엔티티
     * @param maxHp 최대 체력
     */
    public HealthComponent(Entity owner, int maxHp) {
        this.owner = owner;
        this.hp = new HP(maxHp, maxHp);
    }

    /**
     * 엔티티의 체력을 감소 (데미지 처리).
     * 소유자가 무적 상태인 플레이어이면 데미지를 무시
     * 데미지를 입은 후 플레이어는 일시적인 무적 상태가 됨
     *
     * @param amount 감소시킬 체력의 양
     * @return 생존 시 true, 사망 시 false
     */
    public boolean decreaseHealth(double amount) {
        // 소유자가 무적 상태인 우주선이면 데미지 무시
        if (owner instanceof ShipEntity && ((ShipEntity) owner).isInvincible()) {
            return true; // 살아있음, 데미지 없음
        }

        hp.setCurrentHp(hp.getCurrentHp() - amount);

        if (hp.getCurrentHp() <= 0) {
            return false; // 사망
        }

        // 살아있고 플레이어인 경우, 일시적 무적 상태 활성화
        if (owner instanceof ShipEntity) {
            ((ShipEntity) owner).activateInvincibility();
        }

        return true; // 생존
    }

    /**
     * 체력을 최대치로 초기화
     */
    public void reset() {
        hp.setCurrentHp(hp.getMAX_HP());
    }

    /**
     * 체력을 완전히 회복
     */
    public void fullyHeal() {
        hp.setCurrentHp(hp.getMAX_HP());
    }

    /**
     * HP 객체를 반환
     *
     * @return HP 객체
     */
    public HP getHp() {
        return hp;
    }

    public boolean isAlive() {
        return hp.getCurrentHp() > 0;
    }

    public int getCurrentHealth() {
        return (int) hp.getCurrentHp();
    }

    /**
     * 체력을 지정된 양만큼 증가 최대 체력을 초과할 수 없음
     *
     * @param amount 증가시킬 체력의 양
     */
    public void increaseHealth(int amount) {
        hp.setCurrentHp(Math.min(hp.getMAX_HP(), hp.getCurrentHp() + amount));
    }

    public int getMaxHp() {
        return (int) hp.getMAX_HP();
    }
}
