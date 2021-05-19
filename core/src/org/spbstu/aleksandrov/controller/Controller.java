package org.spbstu.aleksandrov.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.entities.Entity;
import org.spbstu.aleksandrov.model.entities.Rocket;
import org.spbstu.aleksandrov.view.WorldRenderer;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.Input.Keys.LEFT;

public class Controller {

    private final MyWorld myWorld;

    public void processInput() {

        Body rocketBody = myWorld.getPhysicBodies().get(0);
        Rocket rocket = (Rocket) myWorld.getEntities().get(0);
        float x = 0;
        float y = (float) (2 * Math.cos(rocketBody.getAngle()));

        boolean keyPressed = false;
        boolean up = (Gdx.input.isKeyPressed(W) || (Gdx.input.isKeyPressed(A) && Gdx.input.isKeyPressed(D))) ||
                (Gdx.input.isKeyPressed(UP) || (Gdx.input.isKeyPressed(LEFT) && Gdx.input.isKeyPressed(RIGHT)));
        boolean left = ((Gdx.input.isKeyPressed(A)) && !(Gdx.input.isKeyPressed(D))) ||
                ((Gdx.input.isKeyPressed(LEFT)) && !(Gdx.input.isKeyPressed(RIGHT)));
        boolean right = ((Gdx.input.isKeyPressed(D)) && !(Gdx.input.isKeyPressed(A))) ||
                ((Gdx.input.isKeyPressed(RIGHT)) && !(Gdx.input.isKeyPressed(LEFT)));

        if (rocket.getFuel() > 0) {

            if (up) {
                rocket.setState(Rocket.State.FLYING_UP);
                keyPressed = true;
                y = (float) (2 * Math.cos(rocketBody.getAngle()));
            }

            if (left) {
                rocket.setState(Rocket.State.FLYING_LEFT);
                keyPressed = true;
                x = (float) Math.PI / -12f;
                if (!up) y = y / 1.25f;
            }

            if (right) {
                rocket.setState(Entity.State.FLYING_RIGHT);
                keyPressed = true;
                x = (float) Math.PI / 12f;
                if (!up) y = y / 1.25f;
            }

            if (keyPressed) {
                rocketBody.applyForceToCenter(
                        new Vector2(rocketBody.getMass() * (x * 60), rocketBody.getMass() * (y * 11)), true);
                if (!WorldRenderer.DEBUG) rocket.decreaseFuel();

            }
        }
    }

    public Controller(MyWorld myWorld) {
        this.myWorld = myWorld;
    }

}
