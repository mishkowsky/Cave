package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public class Coin extends Entity {

    public Coin(Vector2 position) {
        super(position);
    }

    @Override
    public String toString() {
        return "coin";
    }
}
