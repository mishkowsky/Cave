package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;

public class Rocket extends Entity {

    private float angle = 0;
    private float fuel = 100f;

    public Rocket(Vector2 position) {
        super(position);
    }

    public void decreaseFuel() {
        float fuelConsumption = 0.1f;
        this.fuel -= fuelConsumption;
    }

    public void setFullFuel() {
        this.fuel = 100f;
    }

    public void setAngle() {
        switch (state) {
            case FLYING_UP :
            case IDLE :
                if (angle != 0) {
                    if (angle > 0) angle -= 1f; else angle += 1f;
                }
                break;
            case FLYING_RIGHT :
                if (angle != -15) angle -= 1f;
                break;
            case FLYING_LEFT :
                if (angle != 15) angle += 1f;
                break;
        }
    }

    @Override
    public String toString() {
        return "rocket";
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }
}
