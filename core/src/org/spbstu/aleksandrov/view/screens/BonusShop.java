package org.spbstu.aleksandrov.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.Player;
import org.spbstu.aleksandrov.model.entities.Bonus;
import org.spbstu.aleksandrov.view.WorldRenderer;

import java.util.Map;

import static org.spbstu.aleksandrov.model.entities.Bonus.Type.FUEL;

public class BonusShop implements Screen {

    private final Game game;
    private final MyWorld myWorld;
    private final Player player;
    private final Map<Bonus.Type, Integer> inventory;
    private final WorldRenderer renderer;
    private Label amount;
    private Label applied;
    private final Stage stage;
    private final Screen lastScreen;

    public BonusShop(Game game, MyWorld myWorld, Screen lastScreen) {

        this.game = game;
        this.myWorld = myWorld;
        this.player = myWorld.getPlayer();
        this.inventory = myWorld.getPlayer().getInventory();
        this.renderer = new WorldRenderer(myWorld);
        this.lastScreen = lastScreen;
        this.stage = new Stage(new ScreenViewport());
        create();

    }

    private void create() {

        float height = stage.getHeight();
        float width = stage.getWidth();

        //TODO change amount in the inventory when applied
        Button fuel = GameOverScreen.createButton("fuel");
        fuel.setScale(0.5f);

        if (inventory.get(FUEL) == 0) fuel.setDisabled(true);

        fuel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (player.getCurrentBonuses().get(FUEL)) player.editInventory(FUEL, 1);
                else player.editInventory(FUEL, -1);
                player.changeCurrentBonus(FUEL);

                System.out.println("Button fuel Pressed");
            }
        });

        Button buyFuel = GameOverScreen.createButton("buyFuel");
        buyFuel.setScale(0.5f);

        if (myWorld.getPlayer().getBalance() < 5) buyFuel.setDisabled(true);

        buyFuel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                player.editInventory(FUEL, 1);
                player.changeBalance(-5);

                System.out.println("Button buyFuel Pressed");
            }
        });

        Button esc = GameOverScreen.createButton("esc");
        esc.setScale(0.5f);
        esc.setPosition(width * 0.7f, height * 0.7f);

        esc.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                game.setScreen(lastScreen);

                System.out.println("Button esc Pressed");
            }
        });

        //TODO add text information about amount in the inventory & current status of the bonus

        Table menuTable = new Table();

        BitmapFont font;
        Texture fontT = new Texture(Gdx.files.internal("android/assets/font.png"));
        fontT.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("android/assets/font.fnt"), new TextureRegion(fontT), false);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.setUseIntegerPositions(false);
        font.setColor(0f, 0f, 0f, 1f);
        font.getData().setScale(0.5f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, new Color(0f, 0f, 0f, 1f));

        menuTable.add(fuel).space(0.1f);
        menuTable.row();

        amount = new Label("In inventory: " + player.getInventory().get(FUEL), labelStyle);
        menuTable.add(amount);
        menuTable.row();

        applied = new Label("Current status: " + player.getCurrentBonuses().get(FUEL), labelStyle);
        menuTable.add(applied);
        menuTable.row();

        menuTable.add(buyFuel).space(0.1f);

        menuTable.setFillParent(true);

        if (WorldRenderer.DEBUG) menuTable.debug();

        stage.addActor(menuTable);
        stage.addActor(esc);

        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        amount.setText("In inventory: " + player.getInventory().get(FUEL));

        String appliedText;
        if (player.getCurrentBonuses().get(FUEL)) appliedText = "applied";
        else appliedText = "not applied";
        applied.setText("Current status: " + appliedText);

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
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
