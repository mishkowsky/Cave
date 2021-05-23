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
    private Button fuel;
    private Button buyFuel;
    private Button esc;
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

        fuel = Utils.createButton("fuel");
        fuel.setScale(1f);

        fuel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (player.getCurrentBonuses().get(FUEL)) player.editInventory(FUEL, 1);
                else player.editInventory(FUEL, -1);
                player.changeCurrentBonus(FUEL);

                System.out.println("Button fuel Pressed");
            }
        });

        buyFuel = Utils.createButton("buyFuel");
        buyFuel.setScale(1f);

        if (myWorld.getPlayer().getBalance() < 5) buyFuel.setDisabled(true);

        buyFuel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                player.editInventory(FUEL, 1);
                player.changeBalance(-5);

                System.out.println("Button buyFuel Pressed");
            }
        });

        esc = Utils.createButton("esc");
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
        //menuTable.setScale(0.5f);

        BitmapFont font = Utils.createFont();
        font.getData().setScale(0.5f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, new Color(0f, 0f, 0f, 1f));

        menuTable.add(fuel);
        menuTable.row();

        amount = new Label("In inventory: " + player.getInventory().get(FUEL), labelStyle);
        menuTable.add(amount);
        menuTable.row();

        applied = new Label("Current status: " + player.getCurrentBonuses().get(FUEL), labelStyle);
        menuTable.add(applied);
        menuTable.row();

        menuTable.add(buyFuel);

        menuTable.setFillParent(true);

        if (WorldRenderer.DEBUG) menuTable.debug();

        stage.addActor(menuTable);
        stage.addActor(esc);

        Gdx.input.setInputProcessor(stage);
    }

    private void updateButtonStatus() {
        buyFuel.setDisabled(myWorld.getPlayer().getBalance() < 5);
        fuel.setDisabled(inventory.get(FUEL) == 0);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateButtonStatus();

        amount.setText("In inventory: " + player.getInventory().get(FUEL));

        String appliedText;
        if (player.getCurrentBonuses().get(FUEL)) appliedText = "Bonus is applied";
        else appliedText = "Bonus is not applied";
        applied.setText(appliedText);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

        stage.getCamera().viewportWidth = width;
        stage.getCamera().viewportHeight = height;
        stage.getViewport().update(width, height, true);

        float height1 = stage.getHeight();
        float width1 = stage.getWidth();
        esc.setPosition(width1 * 0.7f, height1 * 0.7f);

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
