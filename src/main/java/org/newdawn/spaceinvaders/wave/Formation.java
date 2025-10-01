package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.entity.SpawnInfo;

import java.util.ArrayList;
import java.util.List;

public class Formation {
    private final List<SpawnInfo> spawnList = new ArrayList<>();

    public void add(SpawnInfo spawnInfo) {
        spawnList.add(spawnInfo);
    }

    public List<SpawnInfo> getSpawnList() {
        return spawnList;
    }
}
