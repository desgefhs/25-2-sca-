package org.newdawn.spaceinvaders;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.newdawn.spaceinvaders.core.Game;

public class GameTest {
    @Test
    public void testGameInstantiation() {
        Game game = new Game();
        assertNotNull(game, "Game object should be created successfully.");
    }
}