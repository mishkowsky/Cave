package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public abstract class Entity {

    public enum State {
        IDLE, POP, FLYING_UP, FLYING_RIGHT, FLYING_LEFT
    }

    Vector2 position;
    State state = State.IDLE;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Entity(Vector2 position) {
        this.position = position;
    }
}
