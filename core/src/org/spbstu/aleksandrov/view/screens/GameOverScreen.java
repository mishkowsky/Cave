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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.view.WorldRenderer;


public class GameOverScreen implements Screen {

    private Game game;
    private MyWorld myWorld;
    private final WorldRenderer renderer;
    private Stage stage;

    public GameOverScreen(Game game, MyWorld myWorld) {

        this.game = game;
        this.myWorld = myWorld;
        this.renderer = new WorldRenderer(myWorld);
        create();

    }

    private void create() {

        TextureAtlas mAtlas = new TextureAtlas("packed/restart/restart_button.atlas");
        TextureRegionDrawable drawableUp = new TextureRegionDrawable( mAtlas.findRegion("restart_button") );
        TextureRegionDrawable drawableDown = new TextureRegionDrawable( mAtlas.findRegion("restart_button") );
        TextureRegionDrawable drawableChecked = new TextureRegionDrawable( mAtlas.findRegion("restart_button") );

        Button.ButtonStyle btnStyle = new Button.ButtonStyle(drawableUp, drawableDown, drawableChecked);
        Button restart = new Button(btnStyle);
        restart.setPosition(20,20);

        restart.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {

                myWorld.respawn();
                game.setScreen(new GameScreen(game));

                System.out.println("Button restart Pressed");
            }
        });

        stage = new Stage(new ScreenViewport());
        stage.addActor(restart);

        Gdx.input.setInputProcessor(stage);
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
