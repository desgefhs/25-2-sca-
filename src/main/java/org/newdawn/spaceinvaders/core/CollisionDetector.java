package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;

import java.util.List;

/**
 * 엔티티 간의 충돌을 감지하며, 충돌이 발생하면 각 엔티티의 충돌 처리 로직을 호출하는 클래스입니다.
 */
public class CollisionDetector {

    /**
     * 주어진 엔티티 목록 내에서 모든 가능한 쌍에 대해 충돌을 검사합니다.
     * 충돌이 감지되면, 두 엔티티 각각의 {@link Entity#collidedWith(Entity)} 메소드를 호출하여
     * 충돌을 처리하도록 합니다.
     *
     * @param entities 충돌 검사를 수행할 엔티티 목록
     */
    public void checkCollisions(List<Entity> entities) {
        for (int p = 0; p < entities.size(); p++) {
            for (int s = p + 1; s < entities.size(); s++) {
                Entity me = entities.get(p);
                Entity him = entities.get(s);

                if (me.collidesWith(him)) {
                    me.collidedWith(him);
                    him.collidedWith(me);
                }
            }
        }
    }
}
