package org.spbstu.aleksandrov.model.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RocketTest {

    Rocket rocket = new Rocket();

    @Test
    public void getAngle() {
        rocket.setState(Entity.State.FLYING_LEFT);

        for (int i = 0; i < 16; i++) {
            rocket.setAngle();
        }
        assertEquals(15, rocket.getAngle());

        rocket.setState(Entity.State.FLYING_RIGHT);

        for (int i = 0; i < 5; i++) {
            rocket.setAngle();
        }
        assertEquals(10, rocket.getAngle());

        for (int i = 0; i < 30; i++) {
            rocket.setAngle();
        }
        assertEquals(-15, rocket.getAngle());
    }

    @Test
    public void decreaseFuel() {

        rocket.setDefaultFuelConsumption();
        for (int i = 0; i < 100; i++) {
            rocket.decreaseFuel();
        }
        assertEquals(75, rocket.getFuel());

        rocket.changeDefaultFuelConsumption();
        for (int i = 0; i < 150; i++) {
            rocket.decreaseFuel();
        }
        assertEquals((int) 75f - (0.25f / 1.5f) * 150f,(int) rocket.getFuel());
    }

    @Test
    public void updateFuel() {
        rocket.setRefueling();
        for (int i = 0; i < 100; i++) {
            rocket.updateFuel();
        }
        assertFalse(rocket.getRefueling());
        assertEquals(100, rocket.getFuel());

        rocket.setDefaultFuelConsumption();
        for (int i = 0; i < 200; i++) {
            rocket.decreaseFuel();
        }
        rocket.setRefueling();
        for (int i = 0; i < 25; i++) {
            rocket.updateFuel();
        }
        assertTrue(rocket.getRefueling());
        assertEquals(75, rocket.getFuel());
    }
}
