package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public class Ground extends Entity {

    public Ground(Vector2 position) {
        super(position);
    }

    @Override
    public String toString() {
        return "ground";
    }
}
