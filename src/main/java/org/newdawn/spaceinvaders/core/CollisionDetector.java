package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;

import java.util.List;

/**
 * 엔티티 간의 충돌을 감지하고 처리하는 클래스
 */
public class CollisionDetector {

    /**
     * 주어진 엔티티 목록 내에서 모든 가능한 충돌을 검사
     * 충돌이 감지되면 각 엔티티의 충돌 처리 메서드를 호출
     *
     * @param entities 충돌 검사를 수행할 엔티티 목록
     */
    public void checkCollisions(List<Entity> entities) {
        // 모든 엔티티 쌍에 대해 반복
        for (int p = 0; p < entities.size(); p++) {
            for (int s = p + 1; s < entities.size(); s++) {
                Entity me = entities.get(p);
                Entity him = entities.get(s);

                // 두 엔티티가 충돌하는지 확인
                if (me.collidesWith(him)) {
                    // 각 엔티티에 충돌 사실을 알림
                    me.collidedWith(him);
                    him.collidedWith(me);
                }
            }
        }
    }
}
