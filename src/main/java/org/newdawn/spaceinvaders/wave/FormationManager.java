package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.entity.EntityType;
import org.newdawn.spaceinvaders.entity.MovementPattern;
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
        formations.add(createLineFormation());
        formations.add(createVFormation());
    }

    private Formation createLineFormation() {
        Formation formation = new Formation("Wavy Line");
        for (int i = 0; i < 10; i++) {
            int xPos = 50 + (i * 35);
            // This formation moves in a sine wave and has a 10% chance to upgrade
            formation.add(new SpawnInfo(EntityType.ALIEN, xPos, -50, MovementPattern.SINUSOIDAL, 0.1));
        }
        return formation;
    }

    private Formation createVFormation() {
        Formation formation = new Formation("V-Shape");
        for (int i = 0; i < 5; i++) {
            // This formation moves straight down but has a 30% chance to upgrade
            formation.add(new SpawnInfo(EntityType.ALIEN, (Game.GAME_WIDTH / 2) - 20 - (i * 30), -50 - (i * 30), MovementPattern.STRAIGHT_DOWN, 0.3));
            formation.add(new SpawnInfo(EntityType.ALIEN, (Game.GAME_WIDTH / 2) + 20 + (i * 30), -50 - (i * 30), MovementPattern.STRAIGHT_DOWN, 0.3));
        }
        return formation;
    }

    public Formation getRandomFormation() {
        if (formations.isEmpty()) {
            return new Formation("Empty"); // Return empty formation if none are defined
        }
        return formations.get(random.nextInt(formations.size()));
    }
}
