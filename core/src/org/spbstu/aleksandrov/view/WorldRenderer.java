package org.spbstu.aleksandrov.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.Player;
import org.spbstu.aleksandrov.model.entities.Asteroid;
import org.spbstu.aleksandrov.model.entities.Entity;
import org.spbstu.aleksandrov.model.entities.Platform;
import org.spbstu.aleksandrov.model.entities.Rocket;

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
    private final List<Entity> entitiesForRemove;

    private static final float CAMERA_WIDTH = 45f;
    private static final float CAMERA_HEIGHT = 20f;
    private final double DEGREES_TO_RADIANS = (Math.PI / 180);

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout layout;

    private Texture rocketTexture;
    private Texture groundTexture_0;
    private Texture groundTexture_1;
    private Texture asteroidTexture_0;
    private Texture asteroidTexture_1;
    private Texture asteroidTexture_2;
    private Texture asteroidTexture_3;
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
        this.entitiesForRemove = myWorld.getEntitiesForRemove();

        create();
    }

    private void loadTextures() {
        groundTexture_0 = new Texture(Gdx.files.internal("sprites/ground_0.png"));
        groundTexture_1 = new Texture(Gdx.files.internal("sprites/ground_1.png"));
        rocketTexture = new Texture(Gdx.files.internal("sprites/rocket.png"));
        coinTexture = new Texture(Gdx.files.internal("sprites/coin.png"));
        //bonusTexture = new Texture(Gdx.files.internal("bonus.png"));
        asteroidTexture_0 = new Texture(Gdx.files.internal("sprites/asteroid_0.png"));
        asteroidTexture_1 = new Texture(Gdx.files.internal("sprites/asteroid_1.png"));
        asteroidTexture_2 = new Texture(Gdx.files.internal("sprites/asteroid_2.png"));
        asteroidTexture_3 = new Texture(Gdx.files.internal("sprites/asteroid_3.png"));
        platformTexture = new Texture(Gdx.files.internal("sprites/platform.png"));
    }

    private void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);

        Texture fontT = new Texture(Gdx.files.internal("android/assets/font.png"));
        fontT.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("android/assets/font.fnt"), new TextureRegion(fontT), false);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.setUseIntegerPositions(false);

        layout = new GlyphLayout();

        loadTextures();
        debugRenderer = new Box2DDebugRenderer();
    }

    public void render() {

        moveCamera();

        batch.begin();
        for (Entity entity : entities) {
            drawEntity(entity);
            if (entity instanceof Platform) drawPlatformId((Platform) entity);
        }
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawFuelBar();
        drawBalanceBox();
        shapeRenderer.end();

        batch.begin();
        drawUserData();
        drawText(camera.position);
        batch.end();


        debugRenderer.render(myWorld.getWorld(), camera.combined);
    }

    private void moveCamera() {
        Vector2 position = rocketBody.getPosition();

        camera.position.set(
                position.x - rocketBody.getLinearVelocity().x * 0.1f,
                position.y - rocketBody.getLinearVelocity().y * 0.1f,
                0);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    private void drawEntity(Entity entity) {

        Texture texture = getTexture(entity);

        Body body = getBody(entity);

        //TODO
        //int i = entities.indexOf(entity);
        //Body body = physicBodies.get(i);

        if (entity.getState() == Entity.State.POP) {
            //startAnimation(entity.getPosition());
            startAnimation(body.getPosition());
            if (!(entity instanceof Rocket)) entitiesForRemove.add(entity);
            else {
                Gdx.app.log("Rocket DEAD", "please call next screen");
            }
        } else {
            Sprite sprite = new Sprite(texture);
            Vector2 position = body.getPosition();

            if (entity instanceof Rocket) {
                body.setTransform(body.getPosition().x, body.getPosition().y,
                        rocket.getAngle() * (float) DEGREES_TO_RADIANS);
                sprite.setRotation(rocket.getAngle());
                sprite.setOriginCenter();
                sprite.setPosition(
                        position.x - sprite.getWidth() / 2,
                        position.y - sprite.getHeight() / 2);
            } else
                sprite.setPosition(
                        position.x - sprite.getWidth() / 2 + sprite.getWidth() / 2 * SCALE,
                        position.y - sprite.getHeight() / 2 + sprite.getHeight() / 2 * SCALE);

            sprite.setScale(SCALE);
            sprite.draw(batch);

        }
    }

    private Texture getTexture(Entity entity) {
        switch (entity.getClass().getSimpleName()) {
            case "Rocket":
                return rocketTexture;
            case "Ground":
                switch (myWorld.getId()) {
                    case 0:
                        return groundTexture_0;
                    case 1:
                        return groundTexture_1;
                }
            case "Platform":
                return platformTexture;
            case "Coin":
                return coinTexture;
            case "Asteroid":
                switch (((Asteroid) entity).getId()) {
                    case 0:
                        return asteroidTexture_0;
                    case 1:
                        return asteroidTexture_1;
                    case 2:
                        return asteroidTexture_2;
                    case 3:
                        return asteroidTexture_3;
                }

        }
        return null;
    }

    private void startAnimation(Vector2 position) {
        //TODO burst animation
    }

    private void drawPlatformId(Platform platform) {
        Vector2 position = platform.getPosition();
        String string;
        if (platform.getId() == 0) string = "Start";
        else string = "" + platform.getId();
        font.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(0.01f);

        layout.setText(font, string);
        float height = layout.height;
        float width = layout.width;

        font.draw(batch, string, position.x + platformTexture.getWidth() * SCALE / 2f - width / 2f,
                position.y + platformTexture.getHeight() * SCALE / 2f + height / 2f);
    }

    private void drawFuelBar() {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        Vector3 position = camera.position;

        shapeRenderer.setColor(Color.valueOf("2B2B2B"));
        shapeRenderer.rect(position.x + CAMERA_WIDTH * 0.35f,
                position.y + CAMERA_HEIGHT * 0.45f, 5f, 0.25f);

        String leftColor = countColor((int) rocket.getFuel(), 90);
        String rightColor = countColor((int) rocket.getFuel(), 255);

        shapeRenderer.rect(position.x + CAMERA_WIDTH * 0.35f,
                position.y + CAMERA_HEIGHT * 0.45f, 5f * rocket.getFuel() / 100, 0.25f,
                new Color(Color.valueOf(leftColor)), new Color(Color.valueOf(rightColor)),
                new Color(Color.valueOf(rightColor)), new Color(Color.valueOf(leftColor)));
    }

    private String countColor(int fuel, int i) {

        String red;
        String green;

        if (fuel >= 70) red = Integer.toHexString(7 * i * (100 - fuel) / 210);
        else red = Integer.toHexString(i);
        if (red.length() == 1) red = "0" + red;

        if (fuel < 70) green = Integer.toHexString(i * fuel / 70);
        else green = Integer.toHexString(i);
        if (green.length() == 1) green = "0" + green;

        return red + green + "00";
    }

    private void drawUserData() {

        Vector3 position = camera.position;
        final float cam_h = camera.viewportHeight;
        final float cam_w = camera.viewportWidth;
        float height;
        float width;

        font.setColor(0f, 0f, 0f, 1f);
        font.getData().setScale(0.025f);
        String currentScore = String.valueOf(player.getCurrentScore());
        layout.setText(font, currentScore);
        width = layout.width;

        font.draw(batch, currentScore, camera.position.x - width / 2f,
                camera.position.y + camera.viewportHeight / 2);

        font.getData().setScale(0.015f);
        String highScore = String.valueOf(player.getHighScore());
        layout.setText(font, highScore);
        width = layout.width;

        font.draw(batch, highScore, camera.position.x - width / 2f,
                camera.position.y + camera.viewportHeight / 2 - 25f * SCALE);

        font.getData().setScale(0.015f);
        String balance = String.valueOf(player.getBalance());
        layout.setText(font, balance);
        height = layout.height;
        width = layout.width;
        font.draw(batch, balance, position.x + cam_w * 0.425f - width / 2,
                position.y - cam_h * 0.45f + height / 2);

        Sprite coin = new Sprite(coinTexture);

        coin.setPosition(position.x + cam_w * 0.4f - coin.getWidth() / 2,
                position.y - cam_h * 0.45f - coin.getHeight() / 2);
        coin.setScale(SCALE * 0.5f);
        coin.draw(batch);
    }

    private void drawBalanceBox() {

        Vector3 position = camera.position;
        final float cam_h = camera.viewportHeight;
        final float cam_w = camera.viewportWidth;
        final float rect_h = 15f * SCALE;
        final float rect_w = 50f * SCALE;

        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(position.x + cam_w * 0.425f - rect_w / 2, position.y - cam_h * 0.45f - rect_h / 2,
                rect_w, rect_h);
        shapeRenderer.arc(position.x + cam_w * 0.425f - rect_w / 2, position.y - cam_h * 0.45f,
                rect_h / 2, 90f, 180, 180);
        shapeRenderer.arc(position.x + cam_w * 0.425f + rect_w / 2, position.y - cam_h * 0.45f,
                rect_h / 2, -90f, 180, 180);
    }

    private void drawText(Vector3 position) {

        font.getData().setScale(0.025f);
        font.setColor(0f, 1f, 0f, 1f);
        if (player.getCurrentPlatform() != null) {
            String string =
                    //(int) position.x + "; " + (int) position.y +
                    //System.getProperty("line.separator") + "Fuel: " + (int) rocket.getFuel() +
                    //System.getProperty("line.separator") + "Score: " + player.getCurrentScore() +
                    //System.getProperty("line.separator") + "High score: " + player.getHighScore() +
                    System.getProperty("line.separator") + "State: " + rocket.getState() +
                    System.getProperty("line.separator") + "Current Platform: " + player.getCurrentPlatform().getId();

            font.draw(batch, string, position.x + 50 * SCALE, position.y + 200 * SCALE);
        }
    }

    private Body getBody(Entity entity) {
        int i = entities.indexOf(entity);
        return physicBodies.get(i);
    }
}
