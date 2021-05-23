package org.spbstu.aleksandrov.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.*;
import org.spbstu.aleksandrov.WorldLoader;
import org.spbstu.aleksandrov.controller.Controller;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.entities.*;
import org.spbstu.aleksandrov.view.WorldRenderer;

import java.util.List;
import java.util.Map;

import static org.spbstu.aleksandrov.model.entities.Entity.State.IDLE;

public class GameScreen implements Screen {

    private final Game game;
    private final Controller controller;
    private final MyWorld myWorld;
    private final WorldRenderer renderer;
    private Stage stage;
    private Button bonusShop;
    private Table bonusTable;
    private final List<? extends Entity> entities;

    public GameScreen(Game game) {

        Platform.resetIdCounter();

        this.game = game;

        myWorld = WorldLoader.getWorld();

        this.renderer = new WorldRenderer(myWorld);
        this.entities = myWorld.getEntities();
        this.controller = new Controller(myWorld);

        this.stage = new Stage(new ScreenViewport());
        createStage();
    }

    private void createStage() {

        stage = new Stage(new ScreenViewport());

        bonusTable = new Table();
        bonusTable.left();
        bonusTable.top();

        bonusShop = Utils.createButton("bonus");

        //bonusShop.setTransform(true);
        bonusShop.setScale(0.5f);

        bonusShop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                game.setScreen(new BonusShop(game, myWorld, game.getScreen()));

                System.out.println("Button bonusShop Pressed");
            }
        });

        stage.addActor(bonusTable);
        stage.addActor(bonusShop);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        createStage();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        List<Entity> entitiesForRemove = myWorld.getEntitiesForRemove();
        for (Entity entity : entitiesForRemove) {

            Body body = entity.getBody();
            entities.remove(entity);

            if (body != null) {
                Gdx.app.log("deleteBody", entity.toString());
                myWorld.getWorld().destroyBody(body);
            }
        }

        entitiesForRemove.clear();

        myWorld.stepWorld();

        Rocket rocket = myWorld.getRocket();
        if (rocket.getState() == Entity.State.POP) game.setScreen(new GameOverScreen(game, myWorld, this));

        stage.act(Gdx.graphics.getDeltaTime());

        controller.processInput();
        renderer.render();

        bonusShop.setVisible(rocket.getState() == IDLE && rocket.getAngle() == 0);
        bonusShop.setDisabled(rocket.getState() != IDLE || rocket.getAngle() != 0);

        addEnabledBonuses();
        stage.draw();
    }

    private void addEnabledBonuses() {
        Map<Bonus.Type, Boolean> currentBonuses = myWorld.getPlayer().getCurrentBonuses();
        bonusTable.clear();
        for (Bonus.Type type : Bonus.Type.values()) {
            if (currentBonuses.get(type)) {
                switch (type) {
                    case FUEL:
                        Utils.addImgToTable(bonusTable, "fuel.png", 1f, 90f, 90f);
                }
            }
        }
        bonusTable.setFillParent(true);
        if (WorldRenderer.DEBUG) bonusTable.debug();
    }

    @Override
    public void resize(int width, int height) {
        stage.getCamera().viewportWidth = Gdx.graphics.getWidth();
        stage.getCamera().viewportHeight = Gdx.graphics.getHeight();
        stage.getViewport().update(width, height, true);
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
