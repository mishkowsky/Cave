package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public class Asteroid extends Entity {

    private boolean dynamic;
    private int id;

    public Asteroid(Vector2 position, boolean dynamic, int id) {
        super(position);
        this.dynamic = dynamic;
        this.id = id;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "asteroid";
    }
}
