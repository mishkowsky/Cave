package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public class Ground extends Entity {

    private int id;

    public Ground(Vector2 position) {
        super(position);
    }

    public Ground() {
        super();
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ground_" + id;
    }
}
