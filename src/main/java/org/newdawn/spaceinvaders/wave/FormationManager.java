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
//        //formations.add(createLineFormation()); // 1자 배치
//        formations.add(createVFormation()); // v자 배치
//        formations.add(createDiagonalFormation("Diagonal Left", true)); //대각선 왼쪽
//        formations.add(createDiagonalFormation("Diagonal Right", false));  //대각선 오른쪽
//        formations.add(createCrossFireFormation()); // 4방향 크로스
        formations.add(createConvergingShootersFormation());
    }

    private Formation createConvergingShootersFormation() {
        Formation formation = new Formation("Converging Shooters");
        int yPos = Game.GAME_HEIGHT / 5;
        int shooterWidth = 50; // Approximate width of the shooter sprite

        formation.add(new SpawnInfo(EntityType.THREE_WAY_SHOOTER, 0, yPos, MovementPattern.HORIZ_TO_CENTER_AND_STOP, 0));
        formation.add(new SpawnInfo(EntityType.THREE_WAY_SHOOTER, Game.GAME_WIDTH - shooterWidth, yPos, MovementPattern.HORIZ_TO_CENTER_AND_STOP, 0));

        return formation;
    }

    private Formation createCrossFireFormation() {
        Formation formation = new Formation("Cross Fire");
        int w = Game.GAME_WIDTH;
        int y_spacing = 40;

        // Define X positions for the 4 vertical lanes
        int x1 = w / 8;
        int x2 = w * 3 / 8;
        int x3 = w * 5 / 8;
        int x4 = w * 7 / 8;

        // Create 4 vertical groups in their respective lanes
        for (int i = 0; i < 3; i++) {
            // Group 1 (Top, Lane 1)
            formation.add(new SpawnInfo(EntityType.ALIEN, x1, -50 - (i * y_spacing), MovementPattern.STRAIGHT_DOWN, 0.1));
            // Group 2 (Bottom, Lane 2)
            formation.add(new SpawnInfo(EntityType.ALIEN, x2, 600 + (i * y_spacing), MovementPattern.STRAIGHT_UP, 0.1));
            // Group 3 (Top, Lane 3)
            formation.add(new SpawnInfo(EntityType.ALIEN, x3, -50 - (i * y_spacing), MovementPattern.STRAIGHT_DOWN, 0.1));
            // Group 4 (Bottom, Lane 4)
            formation.add(new SpawnInfo(EntityType.ALIEN, x4, 600 + (i * y_spacing), MovementPattern.STRAIGHT_UP, 0.1));
        }

        return formation;
    }

    private Formation createDiagonalFormation(String name, boolean startFromLeft) {
        Formation formation = new Formation(name);
        for (int i = 0; i < 5; i++) {
            int xPos;
            if (startFromLeft) {
                xPos = 50 + (i * 40);
            } else {
                xPos = (Game.GAME_WIDTH - 50) - (i * 40);
            }
            int yPos = -50 - (i * 40);
            formation.add(new SpawnInfo(EntityType.ALIEN, xPos, yPos, MovementPattern.STRAIGHT_DOWN, 0.1));
        }
        return formation;
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
