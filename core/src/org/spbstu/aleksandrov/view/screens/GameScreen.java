package org.spbstu.aleksandrov.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.spbstu.aleksandrov.DataHolder;
import org.spbstu.aleksandrov.controller.Controller;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.entities.*;
import org.spbstu.aleksandrov.view.WorldRenderer;

import java.util.List;

import static org.spbstu.aleksandrov.model.entities.Entity.State.IDLE;

public class GameScreen implements Screen {

    private final Game game;
    private final Controller controller;
    private final MyWorld myWorld;
    private final WorldRenderer renderer;
    private Stage stage;
    private Button bonusShop;
    private final List<? extends Entity> entities;

    public GameScreen(Game game) {

        Platform.resetIdCounter();

        this.game = game;

        myWorld = DataHolder.getWorld();

        this.renderer = new WorldRenderer(myWorld);
        this.entities = myWorld.getEntities();
        this.controller = new Controller(myWorld);

        this.stage = new Stage();
        createStage();
    }

    private void createStage() {

        stage = new Stage();

        bonusShop = GameOverScreen.createButton("bonus");

        bonusShop.setTransform(true);
        bonusShop.setScale(0.5f);

        float width = stage.getWidth();

        bonusShop.setPosition(width * 0.0125f, width * 0.0125f);

        bonusShop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                game.setScreen(new BonusShop(game, myWorld, game.getScreen()));

                System.out.println("Button bonusShop Pressed");
            }
        });

        stage.addActor(bonusShop);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        createStage();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1f, 1f, 1f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        List<Entity> entitiesForRemove = myWorld.getEntitiesForRemove();

        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        stage.getCamera().update();

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

        stage.act(Gdx.graphics.getDeltaTime());


        controller.processInput();
        renderer.render();

        if (rocket.getState() == IDLE && rocket.getAngle() == 0) {
            bonusShop.setDisabled(false);
            stage.draw();
        } else bonusShop.setDisabled(true);

    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
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
        stage.dispose();
    }
}
