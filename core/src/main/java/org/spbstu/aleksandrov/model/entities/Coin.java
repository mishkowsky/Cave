package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public class Coin extends Entity {

    public Coin(Vector2 position) {
        super(position);
    }

    public Coin() {
        super();
    }

    @Override
    public String toString() {
        return "coin";
    }
}
