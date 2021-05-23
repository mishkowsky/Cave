package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public class Platform extends Entity {

    private final int id;
    private static int i = 0;
    private boolean fuel = true;

    public void refuel() {
        this.fuel = false;
    }

    public int getId() {
        return id;
    }

    public boolean getFuel() {
        return fuel;
    }

    public Platform(Vector2 position) {
        super(position);
        this.id = i;
        i++;
    }

    public Platform() {
        super();
        this.id = i;
        i++;
    }

    public static void resetIdCounter() {
        i = 0;
    }

    @Override
    public String toString() {
        return "platform";
    }
}
