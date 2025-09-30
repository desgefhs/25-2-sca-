package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;

import java.util.List;

/**
 * 엔티티 간의 충돌을 감지,충돌한 엔티티를 처리
 */
public class CollisionDetector {

    //주어진 엔티티 목록 내에서 모든 가능한 충돌을 검사.

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
