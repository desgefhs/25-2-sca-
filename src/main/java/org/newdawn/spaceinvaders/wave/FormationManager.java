package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.entity.EntityType;
import org.newdawn.spaceinvaders.entity.MovementPattern;
import org.newdawn.spaceinvaders.entity.SpawnInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 게임의 모든 적 포메이션(진형)을 생성하고 관리하는 클래스
 * 각 스테이지별로 사용될 포메이션들을 미리 정의하고, 요청 시 무작위 포메이션을 제공
 */
public class FormationManager {

    /** 스테이지별 포메이션 목록을 저장하는 리스트 (5개의 스테이지) */
    private final List<List<Formation>> stages = new ArrayList<>();
    /** 무작위 포메이션 선택을 위한 Random 객체 */
    private final Random random = new Random();

    /**
     * FormationManager 객체를 생성하고 모든 포메이션을 초기화
     */
    public FormationManager() {
        for (int i = 0; i < 5; i++) {
            stages.add(new ArrayList<>());
        }
        createAllFormations();
    }

    /**
     * 지정된 스테이지에 맞는 무작위 포메이션을 반환
     *
     * @param stage 스테이지 번호 (1-5)
     * @return 무작위로 선택된 Formation 객체
     */
    public Formation getRandomFormationForStage(int stage) {
        List<Formation> stageFormations = stages.get(stage - 1);
        if (stageFormations.isEmpty()) {
            return new Formation("Empty"); // 해당 스테이지에 포메이션이 없으면 빈 포메이션 반환
        }
        return stageFormations.get(random.nextInt(stageFormations.size()));
    }

    /**
     * 모든 스테이지의 포메이션을 생성하고 `stages` 리스트에 추가
     */
    private void createAllFormations() {
        // --- 스테이지 1 ---
        stages.get(0).add(createLineFormation());
        stages.get(0).add(createVFormation());

        // --- 스테이지 2 ---
        stages.get(1).add(createDiagonalFormation("Diagonal Left", true));
        stages.get(1).add(createDiagonalFormation("Diagonal Right", false));
        stages.get(1).add(createConvergingShootersFormation(false));
        stages.get(1).add(createLineFormation());

        // --- 스테이지 3 ---
        stages.get(2).add(createConvergingShootersFormation(false));
        stages.get(2).add(createDiagonalFormation("Diagonal Left", true));
        stages.get(2).add(createDiagonalFormation("Diagonal Right", false));
        stages.get(2).add(createCrossFireFormation());

        // --- 스테이지 4 ---
        stages.get(3).add(createBurstShooterFormation(false));
        stages.get(3).add(createCrossFireFormation());
        stages.get(3).add(createBombCarpetFormation());
        stages.get(3).add(createConvergingShootersFormation(true)); // 강화

        // --- 스테이지 5 ---
        stages.get(4).add(createCrossFireFormation());
        stages.get(4).add(createBurstShooterFormation(true)); // 강화
        stages.get(4).add(createBombCarpetFormation());
        stages.get(4).add(createConvergingShootersFormation(true)); // 강화
    }

    // --- 포메이션 정의 메서드들 ---

    /** 물결 모양으로 움직이는 일자 라인 포메이션 */
    private Formation createLineFormation() {
        Formation formation = new Formation("Wavy Line");
        for (int i = 0; i < 10; i++) {
            formation.add(new SpawnInfo(EntityType.ALIEN, 50 + (i * 35), -50, MovementPattern.SINUSOIDAL, 0.1));
        }
        return formation;
    }

    /** V자 모양 포메이션 */
    private Formation createVFormation() {
        Formation formation = new Formation("V-Shape");
        for (int i = 0; i < 5; i++) {
            formation.add(new SpawnInfo(EntityType.ALIEN, (Game.GAME_WIDTH / 2) - 20 - (i * 30), -50 - (i * 30), MovementPattern.STRAIGHT_DOWN, 0.3));
            formation.add(new SpawnInfo(EntityType.ALIEN, (Game.GAME_WIDTH / 2) + 20 + (i * 30), -50 - (i * 30), MovementPattern.STRAIGHT_DOWN, 0.3));
        }
        return formation;
    }

    /** 대각선 모양 포메이션 */
    private Formation createDiagonalFormation(String name, boolean startFromLeft) {
        Formation formation = new Formation(name);
        for (int i = 0; i < 5; i++) {
            int xPos = startFromLeft ? 50 + (i * 40) : (Game.GAME_WIDTH - 50) - (i * 40);
            formation.add(new SpawnInfo(EntityType.ALIEN, xPos, -50 - (i * 40), MovementPattern.STRAIGHT_DOWN, 0.1));
        }
        return formation;
    }

    /** 양쪽에서 중앙으로 모이는 3-Way 슈터 포메이션 */
    private Formation createConvergingShootersFormation(boolean forceUpgrade) {
        Formation formation = new Formation("Converging Shooters" + (forceUpgrade ? "+" : ""));
        int yPos = Game.GAME_HEIGHT / 5;
        int shooterWidth = 50;
        formation.add(new SpawnInfo(EntityType.THREE_WAY_SHOOTER, 0, yPos, MovementPattern.HORIZ_TO_CENTER_AND_STOP, 0, forceUpgrade));
        formation.add(new SpawnInfo(EntityType.THREE_WAY_SHOOTER, Game.GAME_WIDTH - shooterWidth, yPos, MovementPattern.HORIZ_TO_CENTER_AND_STOP, 0, forceUpgrade));
        return formation;
    }

    /** 위아래에서 교차하며 등장하는 포메이션 */
    private Formation createCrossFireFormation() {
        Formation formation = new Formation("Cross Fire");
        int w = Game.GAME_WIDTH;
        int y_spacing = 40;
        int[] lanes = {w / 8, w * 3 / 8, w * 5 / 8, w * 7 / 8};
        for (int i = 0; i < 3; i++) {
            formation.add(new SpawnInfo(EntityType.ALIEN, lanes[0], -50 - (i * y_spacing), MovementPattern.STRAIGHT_DOWN, 0.1));
            formation.add(new SpawnInfo(EntityType.ALIEN, lanes[1], 600 + (i * y_spacing), MovementPattern.STRAIGHT_UP, 0.1));
            formation.add(new SpawnInfo(EntityType.ALIEN, lanes[2], -50 - (i * y_spacing), MovementPattern.STRAIGHT_DOWN, 0.1));
            formation.add(new SpawnInfo(EntityType.ALIEN, lanes[3], 600 + (i * y_spacing), MovementPattern.STRAIGHT_UP, 0.1));
        }
        return formation;
    }

    /** 연사(Burst) 슈터 포메이션 */
    private Formation createBurstShooterFormation(boolean forceUpgrade) {
        Formation formation = new Formation("Burst Shooters" + (forceUpgrade ? "+" : ""));
        for (int i = 0; i < 3; i++) {
            int xPos = 100 + (i * 150);
            formation.add(new SpawnInfo(EntityType.BURST_SHOOTER, xPos, -50, MovementPattern.STRAIGHT_DOWN, 0, forceUpgrade));
        }
        return formation;
    }

    /** 5개의 폭탄이 연속해서 내려오는 포메이션 */
    private Formation createBombCarpetFormation() {
        Formation formation = new Formation("Bomb Carpet");
        int explosionRadius = 120;
        int screenWidth = Game.GAME_WIDTH;
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                int xPos = (screenWidth / 5 * i) + (screenWidth / 10);
                int yPos = -50 - (j * explosionRadius);
                formation.add(new SpawnInfo(EntityType.BOMB, xPos, yPos));
            }
        }
        return formation;
    }
}