package org.spbstu.aleksandrov.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.entities.Platform;
import org.spbstu.aleksandrov.view.WorldRenderer;

public class GameOverScreen implements Screen {

    private final Game game;
    private final MyWorld myWorld;
    private final WorldRenderer renderer;
    private final Stage stage;
    private final Screen lastScreen;

    public GameOverScreen(Game game, MyWorld myWorld, Screen lastScreen) {

        this.game = game;
        this.myWorld = myWorld;
        this.renderer = new WorldRenderer(myWorld);
        this.lastScreen = lastScreen;
        this.stage = new Stage(new ScreenViewport());
        create();

    }

    private void create() {

        Button restart = Utils.createButton("restart");
        restart.setScale(0.5f);

        restart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                game.setScreen(new GameScreen(game));

                System.out.println("Button restart Pressed");
            }
        });

        Button respawn = Utils.createButton("respawn");
        respawn.setScale(0.5f);

        if (myWorld.getPlayer().getBalance() < 5) respawn.setDisabled(true);

        respawn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                myWorld.respawn();
                myWorld.getPlayer().changeBalance(-5);
                game.setScreen(lastScreen);
                System.out.println("Button respawn Pressed");
            }
        });

        BitmapFont font = Utils.createFont();
        font.getData().setScale(0.5f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, new Color(0f, 0f, 0f, 1f));

        Table menuTable = new Table();
        menuTable.debug();
        menuTable.center();

        Label score;
        if (myWorld.getPlayer().getCurrentScore() == myWorld.getPlayer().getHighScore())
            score = new Label("New high score: " + myWorld.getPlayer().getCurrentScore(), labelStyle);
        else score = new Label("Your score: " + myWorld.getPlayer().getCurrentScore(), labelStyle);

        menuTable.add(score).colspan(2);
        menuTable.row();
        menuTable.add(new Label("Continue", labelStyle));
        menuTable.add(new Label("Restart", labelStyle));
        menuTable.row();
        menuTable.add(respawn);
        menuTable.add(restart);
        menuTable.row();
        Utils.addImgToTable(menuTable, "packed/buyFuel/buyFuel_button.png", 1f, 32f, 64f);
        menuTable.setFillParent(true);

        int i = myWorld.getCurrentPlatformIndex();
        if (i + 1 >= myWorld.getEntities().size() || !(myWorld.getEntities().get(i + 1) instanceof Platform))
            respawn.setDisabled(true);

        stage.addActor(menuTable);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getCamera().viewportWidth = width;
        stage.getCamera().viewportHeight = height;
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
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
