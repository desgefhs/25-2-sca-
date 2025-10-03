package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.entity.SpawnInfo;

import java.util.ArrayList;
import java.util.List;


//미리
public class Formation {
    private final String name;
    private final List<SpawnInfo> spawnList = new ArrayList<>();

    public Formation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void add(SpawnInfo spawnInfo) {
        spawnList.add(spawnInfo);
    }

    public List<SpawnInfo> getSpawnList() {
        return spawnList;
    }
}
