package org.spbstu.aleksandrov.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
        this.stage = new Stage();
        create();

    }

    private void create() {

        float height = stage.getHeight();
        float width = stage.getWidth();

        Button restart = createButton("restart");
        restart.setScale(0.5f);

        restart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                game.setScreen(new GameScreen(game));

                System.out.println("Button restart Pressed");
            }
        });

        Button respawn = createButton("respawn");
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

        Table menuTable = new Table();

        menuTable.add(respawn).space(0.1f);
        menuTable.add();
        menuTable.add(restart).space(0.1f);
        menuTable.setFillParent(true);

        int i = myWorld.getCurrentPlatformIndex();
        if (i + 1 >= myWorld.getEntities().size() || !(myWorld.getEntities().get(i + 1) instanceof Platform))
            respawn.setDisabled(true);

        stage.addActor(menuTable);

        Gdx.input.setInputProcessor(stage);
    }

    public static Button createButton(String name) {
        TextureAtlas mAtlas = new TextureAtlas("packed/" + name + "/" + name + "_button.atlas");
        TextureRegionDrawable drawableUp = new TextureRegionDrawable(mAtlas.findRegion(name + "_button"/*+ "_up"*/));
        TextureRegionDrawable drawableDown = new TextureRegionDrawable(mAtlas.findRegion(name + "_button"/*+ "_down"*/));
        TextureRegionDrawable drawableChecked = new TextureRegionDrawable(mAtlas.findRegion(name + "_button"/*+ "_checked"*/));

        Button.ButtonStyle btnStyle = new Button.ButtonStyle(drawableUp, drawableDown, drawableChecked);
        Button button = new Button(btnStyle);
        button.setOrigin(Align.center);
        button.setDebug(WorldRenderer.DEBUG);
        button.setTransform(true);
        return button;
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
