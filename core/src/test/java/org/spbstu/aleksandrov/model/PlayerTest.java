package org.spbstu.aleksandrov.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {


    Player player = new Player();

    @Test
    public void getHighScore() {

        player.addScore(100);
        assertEquals(100, player.getHighScore());
    }
}
