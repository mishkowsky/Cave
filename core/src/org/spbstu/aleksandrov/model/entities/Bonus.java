package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public class Bonus extends Entity {

    public enum Type {
        FUEL
    }

    Type type;

    public Bonus(Vector2 position, Type type) {
        super(position);
        this.type = type;
    }

    @Override
    public String toString() {
        return "bonus";
    }
}
