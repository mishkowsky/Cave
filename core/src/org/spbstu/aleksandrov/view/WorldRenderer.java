package org.spbstu.aleksandrov.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.Player;
import org.spbstu.aleksandrov.model.entities.Entity;
import org.spbstu.aleksandrov.model.entities.Rocket;

import java.util.ArrayList;
import java.util.List;

import static org.spbstu.aleksandrov.model.MyWorld.SCALE;

public class WorldRenderer {

    private final MyWorld myWorld;
    private final Rocket rocket;
    private final Body rocketBody;
    private final Player player;
    private final World world;

    private final List<? extends Entity> entities;
    private final List<Body> physicBodies;

    private static final float CAMERA_WIDTH = 45f;
    private static final float CAMERA_HEIGHT = 20f;
    private final double DEGREES_TO_RADIANS = (Math.PI/180);

    SpriteBatch batch;
    OrthographicCamera camera;
    Box2DDebugRenderer debugRenderer;
    ShapeRenderer shapeRenderer;

    private Texture rocketTexture;
    private Texture groundTexture;
    private Texture asteroidTexture;
    private Texture coinTexture;
    private Texture bonusTexture;
    private Texture platformTexture;

    public WorldRenderer(MyWorld myWorld) {

        this.myWorld = myWorld;

        this.world = myWorld.getWorld();
        this.entities = myWorld.getEntities();
        this.physicBodies = myWorld.getPhysicBodies();
        this.rocket = myWorld.getRocket();
        this.rocketBody = physicBodies.get(0);
        this.player = myWorld.getPlayer();

        create();
    }

    private void loadTextures(){
        groundTexture = new Texture(Gdx.files.internal("ground.png"));
        rocketTexture = new Texture(Gdx.files.internal("rocket.png"));
        //coinTexture = new Texture(Gdx.files.internal("coin.png"));
        //bonusTexture = new Texture(Gdx.files.internal("bonus.png"));
        asteroidTexture = new Texture(Gdx.files.internal("asteroid.png"));
        platformTexture = new Texture(Gdx.files.internal("platform.png"));
    }

    private void create() {
        batch = new SpriteBatch(1000);
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        loadTextures();
        debugRenderer = new Box2DDebugRenderer();
    }

    public void render() {

        moveCamera();

        batch.begin();

        for (Entity entity : entities) {
            drawEntity(entity);
        }


        for (Entity entity : removeEntities) {
            int i = entities.indexOf(entity);
            //myWorld.entities.remove(entity);
            //myWorld.physicBodies.remove(i);
        }

        removeEntities.clear();

        drawUserData();

        drawFont(camera.position);

        batch.end();

        drawFuelBar();

        debugRenderer.render(myWorld.getWorld(), camera.combined);
    }

    private void moveCamera() {
        Vector2 position = rocketBody.getPosition();

        camera.position.set(
                position.x - rocketBody.getLinearVelocity().x * 0.1f ,
                position.y  - rocketBody.getLinearVelocity().y * 0.1f,
                0);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    private final List<Entity> removeEntities = new ArrayList<>();

    private void drawEntity(Entity entity){

        Texture texture = getTexture(entity);
        int i = entities.indexOf(entity);
        Body body = physicBodies.get(i);

        if (body == null || entity.getState() == Entity.State.POP) {
            startAnimation(entity.getPosition());
            if (!(entity instanceof Rocket)) removeEntities.add(entity);
            else {
                Gdx.app.log("Rocket DEAD", "please call next screen"); //TODO call death screen
            }
        } else {
            Sprite sprite = new Sprite(texture);
            Vector2 position = body.getPosition();
            //sprite.setOriginCenter();

            if (entity instanceof Rocket) {
                body.setTransform(body.getPosition().x, body.getPosition().y,
                        rocket.getAngle() * (float) DEGREES_TO_RADIANS);
                sprite.setRotation(rocket.getAngle());
            }

            if (entity instanceof Rocket) {
                sprite.setOriginCenter();
                sprite.setPosition(
                        position.x - sprite.getWidth() / 2,
                        position.y - sprite.getHeight() / 2);
            }
            else
                sprite.setPosition(
                    position.x - sprite.getWidth() / 2 + sprite.getWidth() / 2 * SCALE,
                    position.y - sprite.getHeight() / 2 + sprite.getHeight() / 2 * SCALE);

            sprite.setScale(SCALE);
            sprite.draw(batch);

        }
    }

    private Texture getTexture(Entity entity) {
        switch (entity.getClass().getSimpleName()) {
            case "Rocket" : return rocketTexture;
            case "Ground" : return groundTexture;
            case "Platform" : return platformTexture;
            case "Coin" : return coinTexture;
            case "Asteroid" : return asteroidTexture;

        }
        return null;
    }

    private void startAnimation(Vector2 position) {
        //TODO burst animation
    }

    private void drawPlatformId() {
        //TODO
    }

    private void drawFuelBar() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Vector3 position = camera.position;

        //float cameraWidth = camera.viewportWidth;
        //float cameraHeight = camera.viewportHeight;

        shapeRenderer.setColor(Color.valueOf("2B2B2B"));
        shapeRenderer.rect(position.x + 16f, position.y + 9f, 5f, 0.25f);

        String leftColor = countColor((int) rocket.getFuel(), 90);
        String rightColor = countColor((int) rocket.getFuel(), 255);

        shapeRenderer.rect(position.x + 16f, position.y + 9f, 5f * rocket.getFuel() / 100, 0.25f,
                new Color(Color.valueOf(leftColor)), new Color(Color.valueOf(rightColor)),
                new Color(Color.valueOf(rightColor)), new Color(Color.valueOf(leftColor)));

        shapeRenderer.end();
    }

    private String countColor(int fuel, int i) {

        String red;
        String green;

        if (fuel >= 70) red = Integer.toHexString(7 * i * (100 - fuel) / 210); else red = Integer.toHexString(i);
        if (red.length() == 1) red = "0" + red;

        if (fuel < 70) green = Integer.toHexString(i * fuel / 70); else green = Integer.toHexString(i);
        if (green.length() == 1) green = "0" + green;

        return red + green + "00";
    }

    private void drawUserData() {
        //TODO
    }

    //for debug, raw version, doesn't work properly
    private void drawFont(Vector3 position) {
        BitmapFont font;

        Texture fontT = new Texture(Gdx.files.internal("android/assets/font.png"));
        fontT.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        font = new BitmapFont(Gdx.files.internal("android/assets/font.fnt"), new TextureRegion(fontT), false);

        font.getData().setScale(0.05f);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.setColor(0f, 0f, 0f, 1f);

        String string = (int) position.x + "; " + (int) position.y +
                System.getProperty("line.separator") + "Fuel: " + (int) rocket.getFuel() +
                System.getProperty("line.separator") + "Score: " + player.getCurrentScore() +
                System.getProperty("line.separator") + "High score: " + player.getHighScore() +
                System.getProperty("line.separator") + "State: " + rocket.getState() +
                System.getProperty("line.separator") + "CurPlat: " + player.getCurrentPlatform().getId();

        font.draw(batch, string, position.x - 50  * SCALE, position.y + 200 * SCALE);
    }
}
