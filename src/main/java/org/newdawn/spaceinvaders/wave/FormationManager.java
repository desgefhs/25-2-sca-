package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.entity.EntityType;
import org.newdawn.spaceinvaders.entity.SpawnInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



// 적 배치 목록
public class FormationManager {

    private final List<Formation> formations = new ArrayList<>();
    private final Random random = new Random();

    public FormationManager() {
        createFormations();
    }

    private void createFormations() {
        // Formation 1: Straight Line
        Formation lineFormation = new Formation("Straight Line");
        for (int i = 0; i < 10; i++) {
            int xPos = 50 + (i * 35);
            lineFormation.add(new SpawnInfo(EntityType.ALIEN, xPos, -50));
        }
        formations.add(lineFormation);

        // Formation 2: V-Shape
        Formation vFormation = new Formation("V-Shape");
        for (int i = 0; i < 5; i++) {
            vFormation.add(new SpawnInfo(EntityType.ALIEN, (Game.GAME_WIDTH / 2) - 20 - (i * 30), -50 - (i * 30)));
            vFormation.add(new SpawnInfo(EntityType.ALIEN, (Game.GAME_WIDTH / 2) + 20 + (i * 30), -50 - (i * 30)));
        }
        formations.add(vFormation);
    }

    public Formation getRandomFormation() {
        if (formations.isEmpty()) {
            return new Formation("Empty"); // Return empty formation if none are defined
        }
        return formations.get(random.nextInt(formations.size()));
    }
}
