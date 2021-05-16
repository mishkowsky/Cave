package org.spbstu.aleksandrov.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import org.spbstu.aleksandrov.DataHolder;
import org.spbstu.aleksandrov.controller.Controller;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.Player;
import org.spbstu.aleksandrov.model.entities.*;
import org.spbstu.aleksandrov.view.WorldRenderer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.spbstu.aleksandrov.model.MyWorld.SCALE;

public class GameScreen implements Screen {

    private final MyWorld myWorld_1 = new MyWorld(
            new Player(),
            List.of(
                    new Rocket(new Vector2((28.5f) * SCALE, (3700 + 25f) * SCALE)),
                    new Ground(new Vector2(0, 0)),
                    new Platform(new Vector2(0, 3700 * SCALE)), new Platform(new Vector2(70 * SCALE, 2500 * SCALE)),
                    new Asteroid(new Vector2(440 * SCALE, 3135 * SCALE), true, 2)
            ), 0
    );

    private static final MyWorld myWorld_0 = new MyWorld(
            new Player(),
            List.of(
                    new Rocket(new Vector2( (572 + 28.5f) * SCALE,(7731 + 30f) * SCALE)),
                    new Ground(new Vector2(0,0)),
                    new Platform(new Vector2(572 * SCALE, 7731 * SCALE)),
                    new Platform(new Vector2(520 * SCALE, 6970 * SCALE)),
                    new Platform(new Vector2(570 * SCALE, 6270 * SCALE)),
                    new Platform(new Vector2(1319 * SCALE, 5619 * SCALE)),
                    new Platform(new Vector2(1174 * SCALE, 4782 * SCALE)),
                    new Platform(new Vector2(313 * SCALE, 4582 * SCALE)),
                    new Platform(new Vector2(310 * SCALE, 3538 * SCALE)),
                    new Platform(new Vector2(1388 * SCALE, 2734 * SCALE)),
                    new Platform(new Vector2(375 * SCALE, 2243 * SCALE)),
                    new Platform(new Vector2( 636* SCALE, 1511 * SCALE)),
                    new Platform(new Vector2(1018 * SCALE, 548 * SCALE)),
                    new Asteroid(new Vector2(570 * SCALE, 7239 * SCALE), false, 1),
                    new Asteroid(new Vector2(700 * SCALE, 7098 * SCALE), false, 1),
                    new Asteroid(new Vector2(263 * SCALE, 6642 * SCALE), true, 2),
                    new Asteroid(new Vector2( 1315 * SCALE, 6303 * SCALE), true, 3),
                    new Asteroid(new Vector2(1230 * SCALE, 1724 * SCALE), false, 1),
                    new Asteroid(new Vector2(1306 * SCALE, 5169 * SCALE), true, 3),
                    new Asteroid(new Vector2(555 * SCALE, 4781 * SCALE), false, 0),
                    new Asteroid(new Vector2(293 * SCALE, 3863 * SCALE), true, 2),
                    new Asteroid(new Vector2(557 * SCALE, 3520 * SCALE), true, 2),
                    new Asteroid(new Vector2(915 * SCALE, 2536 * SCALE), true, 1),
                    new Asteroid(new Vector2(655 * SCALE, 2533 * SCALE), true, 2)

            ), 1
    );

    private final Game game;
    private final Controller controller;
    private final MyWorld myWorld;
    private final WorldRenderer renderer;
    private final List<? extends Entity> entities;

    public GameScreen(Game game) {
        Platform.resetIdCounter();
        this.game = game;

        myWorld = DataHolder.getWorld();

        this.renderer = new WorldRenderer(myWorld);
        this.entities = myWorld.getEntities();
        this.controller = new Controller(myWorld);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1f, 1f, 1f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        myWorld.stepWorld();
        Rocket rocket = myWorld.getRocket();

        if (rocket.getState() == Entity.State.POP) game.setScreen(new GameOverScreen(game, myWorld, this));


        controller.processInput();
        renderer.render();


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
