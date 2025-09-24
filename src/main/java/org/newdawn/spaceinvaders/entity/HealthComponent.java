package org.newdawn.spaceinvaders.entity;

/**
 * 엔티티의 체력과 관련된 모든 로직을 관리하는 컴포넌트.
 * 데미지 처리, 생존 여부 확인, 체력 초기화 .
 */
public class HealthComponent {

    private HP hp;

    public HealthComponent(int maxHp) {
        this.hp = new HP(maxHp, maxHp);
    }

    // 데미지 처리
    public boolean decreaseHealth(double amount) {
        hp.setCurrentHp(hp.getCurrentHp() - amount);
        return hp.getCurrentHp() > 0;
    }

    //체력 초기화
    public void reset() {
        hp.setCurrentHp(hp.getMAX_HP());
    }

    public HP getHp() {
        return hp;
    }


    //생존 여부 확인
    public boolean isAlive() {
        return hp.getCurrentHp() > 0;
    }
}
