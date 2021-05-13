package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public class Asteroid extends Entity {

    private boolean dynamic;

    public Asteroid(Vector2 position, boolean dynamic) {
        super(position);
        this.dynamic = dynamic;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public String toString() {
        return "asteroid";
    }
}
