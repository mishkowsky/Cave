package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class Rocket extends Entity {

    private float angle = 0;
    private float fuel = 100f;
    private float fuelConsumption;
    private boolean refueling;

    public Rocket(Vector2 position) {
        super(position);
        setDefaultFuelConsumption();
    }

    public Rocket() {
        super(new Vector2(0, 0));
    }

    public void decreaseFuel() {
        this.fuel -= fuelConsumption;
    }

    public void updateFuel() {
        if (fuel >= 100f) {
            fuel = 100f;
            refueling = false;
        }
        if (refueling) fuel += 1f;
    }

    public void changeDefaultFuelConsumption() {
        fuelConsumption = 0.25f / 1.5f;
    }

    public void setDefaultFuelConsumption() {
        fuelConsumption = 0.25f;
    }

    public void setAngle() {
        switch (state) {
            case FLYING_UP:
            case IDLE:
                if (angle != 0) {
                    if (angle > 0) angle -= 1f;
                    else angle += 1f;
                }
                break;
            case FLYING_RIGHT:
                if (angle != -15) angle -= 1f;
                break;
            case FLYING_LEFT:
                if (angle != 15) angle += 1f;
                break;
        }
    }

    @Override
    public void createBody(World world) {
        super.createBody(world);
        super.body.setType(BodyDef.BodyType.DynamicBody);
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

    public void setRefueling() {
        refueling = true;
    }

    public boolean getRefueling() {
        return refueling;
    }

    public float getFuelConsumption() {
        return fuelConsumption;
    }
}
