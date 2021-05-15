package org.spbstu.aleksandrov.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.Player;
import org.spbstu.aleksandrov.model.entities.*;
import org.spbstu.aleksandrov.view.WorldRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static org.spbstu.aleksandrov.model.MyWorld.SCALE;

public class GameScreen implements Screen {

    private final MyWorld myWorld = new MyWorld(
            new Player(),
            List.of(
                new Rocket(new Vector2( (28.5f) * SCALE,(3700 + 25f) * SCALE)),
                new Ground(new Vector2(0,0)),
                new Platform(new Vector2(0,3700 * SCALE)), new Platform(new Vector2(70 * SCALE, 2500 * SCALE)),
                new Asteroid(new Vector2(440 * SCALE, 3135 * SCALE), true, 2)
            ), 0
    );

    private final Game game;
    private final WorldRenderer renderer = new WorldRenderer(myWorld);
    private final List<? extends Entity> entities;

    public GameScreen(Game game) {
        this.game = game;
        this.entities = myWorld.getEntities();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1f, 1f, 1f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        myWorld.stepWorld();

        Rocket rocket = myWorld.getRocket();
        if (rocket.getState() == Entity.State.POP) game.setScreen(new GameOverScreen(game, myWorld));

        List<Entity> entitiesForRemove = myWorld.getEntitiesForRemove();

        for (Entity entity : entitiesForRemove) {

                int i = entities.indexOf(entity);
                Body body = myWorld.getPhysicBodies().get(i);
                entities.remove(entity);

                if (body != null) {
                    Gdx.app.log("deleteBody", entity.toString());
                    myWorld.getWorld().destroyBody(body);
                }
                myWorld.getPhysicBodies().remove(i);
        }

        entitiesForRemove.clear();

        processInput();
        renderer.render();
    }

    private void processInput() {

        Body rocketBody = myWorld.getPhysicBodies().get(0);
        Rocket rocket = (Rocket) myWorld.getEntities().get(0);
        float x = 0;
        float y = (float) (2 * Math.cos(rocketBody.getAngle()));

        boolean keyPressed = false;
        boolean up = (Gdx.input.isKeyPressed(W) || (Gdx.input.isKeyPressed(A) && Gdx.input.isKeyPressed(D))) ||
                (Gdx.input.isKeyPressed(UP) || (Gdx.input.isKeyPressed(LEFT) && Gdx.input.isKeyPressed(RIGHT)));
        boolean left = ((Gdx.input.isKeyPressed(A)) && !(Gdx.input.isKeyPressed(D)))||
                ((Gdx.input.isKeyPressed(LEFT)) && !(Gdx.input.isKeyPressed(RIGHT)));
        boolean right = ((Gdx.input.isKeyPressed(D)) && !(Gdx.input.isKeyPressed(A)))||
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
                rocket.decreaseFuel();
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
