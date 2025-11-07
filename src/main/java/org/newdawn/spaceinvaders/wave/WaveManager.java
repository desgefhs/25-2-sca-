package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.BossEntity;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.Pet.AttackPetEntity;
import org.newdawn.spaceinvaders.entity.Pet.BuffPetEntity;
import org.newdawn.spaceinvaders.entity.Pet.DefensePetEntity;
import org.newdawn.spaceinvaders.entity.Pet.HealPetEntity;
import org.newdawn.spaceinvaders.entity.Pet.PetType;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.weapon.DefaultGun;
import org.newdawn.spaceinvaders.entity.weapon.Laser;
import org.newdawn.spaceinvaders.entity.weapon.Shotgun;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.core.GameState;

public class WaveManager {

    private final GameManager gameManager;
    private final FormationManager formationManager;

    private int wave = 0;
    private int formationsPerWave;
    private int formationsSpawnedInWave;
    private boolean healingAreaSpawnedForWave = false;
    private long nextFormationSpawnTime = 0;

    public WaveManager(GameManager gameManager, FormationManager formationManager) {
        this.gameManager = gameManager;
        this.formationManager = formationManager;
    }

    public void startFirstWave() {
        wave = 0;
        startNextWave();
    }

    public void skipToNextBossWave() {
        this.wave = ((this.wave / 5) * 5) + 4; // Set to the wave before the next boss
        startNextWave();
    }

    public void startNextWave() {
        wave++;
        healingAreaSpawnedForWave = false;
        if (wave > 25) {
                        gameManager.notifyWin();
                        return;
                    }
                    gameManager.setMessage("Wave " + wave);        gameManager.setMessageEndTime(System.currentTimeMillis() + 1000);

        if (wave % 5 == 0) {
            gameManager.getSoundManager().stopSound("gamebackground");
            gameManager.getSoundManager().loopSound("boss1");
        } else if ((wave - 1) % 5 == 0 && wave > 1) {
            gameManager.getSoundManager().stopSound("boss1");
            gameManager.getSoundManager().loopSound("gamebackground");
        } else if (wave == 1) {
            gameManager.getSoundManager().loopSound("gamebackground");
        }

        PlayerData currentPlayer = gameManager.getPlayerManager().getCurrentPlayer();
        String equippedWeaponName = currentPlayer.getEquippedWeapon();
        Weapon selectedWeapon;
        if (equippedWeaponName != null) {
            switch (equippedWeaponName) {
                case "Shotgun":
                    selectedWeapon = new Shotgun();
                    break;
                case "Laser":
                    selectedWeapon = new Laser();
                    break;
                default:
                    selectedWeapon = new DefaultGun();
                    break;
            }
        } else {
            selectedWeapon = new DefaultGun();
        }

        gameManager.getEntityManager().initShip(gameManager.getPlayerManager().getPlayerStats(), selectedWeapon);

        if (currentPlayer != null && currentPlayer.getEquippedPet() != null) {
            try {
                ShipEntity playerShip = gameManager.getShip();
                PetType petType = PetType.valueOf(currentPlayer.getEquippedPet());
                switch (petType) {
                    case ATTACK:
                        gameManager.addEntity(new AttackPetEntity(gameManager, playerShip, playerShip.getX(), playerShip.getY()));
                        break;
                    case DEFENSE:
                        DefensePetEntity defensePet = new DefensePetEntity(gameManager, playerShip, playerShip.getX(), playerShip.getY());
                        gameManager.addEntity(defensePet);
                        playerShip.setShield(true, defensePet::resetAbilityCooldown);
                        defensePet.resetAbilityCooldown();
                        break;
                    case HEAL:
                        gameManager.addEntity(new HealPetEntity(gameManager, playerShip, playerShip.getX(), playerShip.getY()));
                        break;
                    case BUFF:
                        gameManager.addEntity(new BuffPetEntity(gameManager, playerShip, playerShip.getX(), playerShip.getY()));
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Attempted to spawn unknown pet type: " + currentPlayer.getEquippedPet());
            }
        }

        if (wave % 5 == 0) {
            formationsPerWave = 1;
            formationsSpawnedInWave = 0;
            spawnBossNow();
            formationsSpawnedInWave = 1;
        } else {
            int stage = ((wave - 1) / 5) + 1;
            switch (stage) {
                case 1: formationsPerWave = 3; break;
                case 2: formationsPerWave = 4; break;
                case 3: formationsPerWave = 5; break;
                case 4: formationsPerWave = 6; break;
                case 5: formationsPerWave = 7; break;
                default: formationsPerWave = 3; break;
            }
            gameManager.setMessage("Wave " + wave);
            formationsSpawnedInWave = 0;
            spawnNextFormationInWave();
        }

        gameManager.setCurrentState(GameState.Type.PLAYING);
    }

    public void spawnNextFormationInWave() {
        if (formationsSpawnedInWave >= formationsPerWave || wave % 5 == 0) {
            return;
        }

        int stage = ((wave - 1) / 5) + 1;
        Formation formation = formationManager.getRandomFormationForStage(stage);

        boolean forceUpgrade = false;
        String formationName = formation.getName();

        if (stage == 4 && formationName.contains("Converging Shooters")) {
            forceUpgrade = true;
        }
        if (stage == 5) {
            if (formationName.contains("Burst Shooters") || formationName.contains("Converging Shooters")) {
                forceUpgrade = true;
            }
        }

        gameManager.getEntityManager().spawnFormation(formation, wave, forceUpgrade);
        formationsSpawnedInWave++;

        if (formationsSpawnedInWave < formationsPerWave) {
            nextFormationSpawnTime = System.currentTimeMillis() + 3000L;
        }
    }

    public void spawnBossNow() {
        gameManager.getEntityManager().getEntities().removeIf(entity -> !(entity instanceof ShipEntity));

        int cycle = (wave - 1) / 5;
        double cycleMultiplier = Math.pow(1.5, cycle);
        int bossHealth = (int) (50 * cycleMultiplier);
        Entity boss = new BossEntity(gameManager, Game.GAME_WIDTH / 2, 50, bossHealth, cycle, wave, false);
        gameManager.addEntity(boss);
        gameManager.getEntityManager().setAlienCount(1);
    }

    public int getWave() {
        return wave;
    }

    public int getFormationsPerWave() {
        return formationsPerWave;
    }

    public int getFormationsSpawnedInWave() {
        return formationsSpawnedInWave;
    }

    public boolean isHealingAreaSpawnedForWave() {
        return healingAreaSpawnedForWave;
    }

    public void setHealingAreaSpawnedForWave(boolean healingAreaSpawnedForWave) {
        this.healingAreaSpawnedForWave = healingAreaSpawnedForWave;
    }

    public long getNextFormationSpawnTime() {
        return nextFormationSpawnTime;
    }
}
