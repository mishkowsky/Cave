package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public class Platform extends Entity {

    private final int id;
    private static int i = 0;
    public boolean fuel = true;

    public void refuel() {
        this.fuel = false;
    }

    public int getId() {
        return id;
    }

    public Platform(Vector2 position) {
        super(position);
        this.id = i;
        i++;
    }

    @Override
    public String toString() {
        return "platform";
    }
}
