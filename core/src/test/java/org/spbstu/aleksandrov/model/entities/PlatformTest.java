package org.spbstu.aleksandrov.model.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlatformTest {

    Platform platform = new Platform();

    @Test
    public void refuel() {
        assertTrue(platform.getFuel());

        platform.refuel();

        assertFalse(platform.getFuel());

        assertEquals(0, platform.getId());

        Platform platform1 = new Platform();
        assertEquals(1, platform1.getId());
    }
}
