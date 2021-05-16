package org.spbstu.aleksandrov.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.view.WorldRenderer;


public class GameOverScreen implements Screen {

    private Game game;
    private MyWorld myWorld;
    private final WorldRenderer renderer;
    private Stage stage;
    private Screen lastScreen;

    public GameOverScreen(Game game, MyWorld myWorld, Screen lastScreen) {

        this.game = game;
        this.myWorld = myWorld;
        this.renderer = new WorldRenderer(myWorld);
        this.lastScreen = lastScreen;
        create();

    }

    private void create() {

        stage = new Stage(new ScreenViewport());

        float height = stage.getHeight();
        float width = stage.getWidth();

        Button restart = createButton("restart");
        restart.setOrigin(Align.center);
        restart.setPosition(width / 2f + 100f, height / 2f);

        restart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                game.setScreen(new GameScreen(game));

                System.out.println("Button restart Pressed");
        }
        });

        Button respawn = createButton("respawn");
        respawn.setOrigin(Align.center);
        respawn.setPosition(width / 2f - 100f, height / 2f);

        respawn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                myWorld.respawn();
                game.setScreen(lastScreen);

                System.out.println("Button respawn Pressed");
            }
        });


        stage.addActor(restart);
        stage.addActor(respawn);

        Gdx.input.setInputProcessor(stage);
    }

    private Button createButton(String name) {
        TextureAtlas mAtlas = new TextureAtlas("packed/" + name + "/" + name + "_button.atlas");
        TextureRegionDrawable drawableUp = new TextureRegionDrawable(mAtlas.findRegion(name + "_button"/*+ "_up"*/));
        TextureRegionDrawable drawableDown = new TextureRegionDrawable(mAtlas.findRegion(name + "_button"/*+ "_down"*/));
        TextureRegionDrawable drawableChecked = new TextureRegionDrawable(mAtlas.findRegion(name + "_button"/*+ "_checked"*/));

        Button.ButtonStyle btnStyle = new Button.ButtonStyle(drawableUp, drawableDown, drawableChecked);

        return new Button(btnStyle);
    }



    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //renderer.render();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

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
