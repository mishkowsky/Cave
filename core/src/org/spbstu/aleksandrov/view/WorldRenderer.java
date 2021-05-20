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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import org.spbstu.aleksandrov.model.MyWorld;
import org.spbstu.aleksandrov.model.Player;
import org.spbstu.aleksandrov.model.entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.spbstu.aleksandrov.model.MyWorld.SCALE;

public class WorldRenderer {

    private final MyWorld myWorld;
    private final Rocket rocket;
    private final Body rocketBody;
    private final Player player;
    private final World world;

    public static boolean DEBUG = true;

    private final List<? extends Entity> entities;
    private final List<Body> physicBodies;
    private final List<Entity> entitiesForRemove;

    private static final float CAMERA_WIDTH = 40f;
    private static final float CAMERA_HEIGHT = 25f;
    private final double DEGREES_TO_RADIANS = (Math.PI / 180);

    private ExtendViewport viewport;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout layout;
    private Stage stage;

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

    private final Map<Animation<TextureRegion>, Pair> currentAnimations = new HashMap<>();
    private final List<Animation<TextureRegion>> finishedAnimations = new ArrayList<>();
    private Animation<TextureRegion> flameAnimation;
    private Animation<TextureRegion> popAnimation;

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

    private void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        viewport = new ExtendViewport(45f, 20f);
        camera = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);

        stage = new Stage(viewport, batch);

        Texture fontT = new Texture(Gdx.files.internal("android/assets/font.png"));
        fontT.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("android/assets/font.fnt"), new TextureRegion(fontT), false);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.setUseIntegerPositions(false);

        layout = new GlyphLayout();

        loadTextures();

        flameAnimation = createAnimation("flame", 10, 12, 0.25f);
        popAnimation = createAnimation("pop", 3, 3, 0.05f);

        debugRenderer = new Box2DDebugRenderer();
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

    private Animation<TextureRegion> createAnimation(String name, int frameCols, int frameRows, float frameDuration) {

        Texture texture = new Texture(Gdx.files.internal("animation/" + name + ".png"));
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / frameCols, texture.getHeight() / frameRows);
        TextureRegion[] textureFrames = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                textureFrames[index++] = tmp[i][j];
            }
        }
        return new Animation<>(frameDuration, textureFrames);
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

        if (DEBUG) drawText(camera.position);
        drawAnimations();
        batch.end();

        drawEnabledBonuses();

        if (DEBUG) debugRenderer.render(myWorld.getWorld(), camera.combined);
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

            Gdx.app.log("start animation", "pop");

            float width = texture.getWidth();
            float height = texture.getHeight();

            Vector2 position = body.getPosition();

            position.x += width * SCALE / 2f;
            position.y += height * SCALE / 2f;

            currentAnimations.put(popAnimation, new Pair(position));

            if (!(entity instanceof Rocket)) entitiesForRemove.add(entity);
            else Gdx.app.log("Rocket DEAD", "please call next screen");
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

    private void drawAnimation(Animation<TextureRegion> animation, Vector2 position, Float stateTime, boolean loop) {

        Gdx.app.log("animation started", position.toString());
        Gdx.app.log("rocket pos", rocketBody.getPosition().toString());

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, loop);

        float height = currentFrame.getRegionHeight();
        float width = currentFrame.getRegionWidth();

        batch.draw(currentFrame, position.x, position.y,
                -width * 0.25f * SCALE / 2f, -height * 0.25f * SCALE / 2f,
                width, height, 0.25f * SCALE, 0.25f * SCALE, 0);
        if (animation.isAnimationFinished(stateTime)) finishedAnimations.add(animation);
        currentAnimations.get(animation).setStateTime(stateTime);
    }

    private void drawAnimations() {
        for (Animation<TextureRegion> animation : currentAnimations.keySet()) {

            Float stateTime = currentAnimations.get(animation).getStateTime();
            Vector2 position = currentAnimations.get(animation).getPosition();
            drawAnimation(animation, position, stateTime, false);
        }

        for (Animation<TextureRegion> animation : finishedAnimations) {
            currentAnimations.remove(animation);
        }
        finishedAnimations.clear();
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

    private void drawEnabledBonuses() {
        //TODO

        Table menuTable = new Table();
        menuTable.left();
        Map<Bonus.Type, Boolean> currentBonuses = player.getCurrentBonuses();
        for (Bonus.Type type : Bonus.Type.values()) {
            if (currentBonuses.get(type)) {
                switch (type) {
                    case FUEL: {
                        /*Gdx.app.log("FUEL", "is added to table");
                        Texture tx = new Texture("fuel.png");
                        Image img = new Image(tx);
                        //img.setScale(1f);
                        menuTable.add(img);
                        menuTable.row();*/
                    }
                }
            }
        }
        //stage.addActor(menuTable);
        //stage.draw();
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
                    "Fuel bonus" + player.getCurrentBonuses().toString() +
                            System.getProperty("line.separator") + "fuel bonuses: " + player.getInventory().get(Bonus.Type.FUEL) +
                            System.getProperty("line.separator") + "fuel consumption" + rocket.getFuelConsumption() +
                            System.getProperty("line.separator") + "State: " + rocket.getState() +
                            System.getProperty("line.separator") + "Current Platform: " + player.getCurrentPlatform().getId();

            font.draw(batch, string, position.x + 50 * SCALE, position.y + 200 * SCALE);
        }
    }

    private Body getBody(Entity entity) {
        int i = entities.indexOf(entity);
        return physicBodies.get(i);
    }

    private static class Pair {

        Vector2 position;
        Float stateTime;

        public Pair(Vector2 position) {
            this.position = position;
            this.stateTime = 0f;
        }

        public Vector2 getPosition() {
            return position;
        }

        public void setPosition(Vector2 position) {
            this.position = position;
        }

        public Float getStateTime() {
            return stateTime;
        }

        public void setStateTime(Float stateTime) {
            this.stateTime = stateTime;
        }
    }
}
