package org.newdawn.spaceinvaders.player;

/**
 * A class to hold statistics and upgrade levels for the player's pet.
 */
public class PetStats {

    private int attackPetLevel = 0;

    public int getAttackPetLevel() {
        return attackPetLevel;
    }

    public void setAttackPetLevel(int attackPetLevel) {
        this.attackPetLevel = attackPetLevel;
    }

    public void increaseAttackPetLevel() {
        this.attackPetLevel++;
    }

    private int defensePetLevel = 0;

    public int getDefensePetLevel() {
        return defensePetLevel;
    }

    public void increaseDefensePetLevel() {
        this.defensePetLevel++;
    }
}
