package org.spbstu.aleksandrov.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import org.spbstu.aleksandrov.model.entities.*;
import org.spbstu.aleksandrov.view.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;
import static org.spbstu.aleksandrov.model.entities.Entity.State.IDLE;
import static org.spbstu.aleksandrov.model.entities.Entity.State.POP;

public class MyWorld implements Cloneable {

    private final Player player;
    private final Rocket rocket;
    private final int id;
    private Body rocketBody;
    private World world;

    private final List<Entity> entities = new ArrayList<>();
    //private final List<Body> physicBodies = new ArrayList<>();
    private final List<Entity> entitiesForRemove = new ArrayList<>();
    private boolean checkDistance = false;
    private int currentPlatformIndex;

    private PhysicsShapeCache physicsShapeCache;

    static final float STEP_TIME = 1f / 60f;
    static final int VELOCITY_ITERATIONS = 8;
    static final int POSITION_ITERATIONS = 3;
    public static final float SCALE = 0.05f;

    float accumulator = 0;

    private void create() {
        Box2D.init();
        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new Listener(this));
        physicsShapeCache = new PhysicsShapeCache("physicbodies.xml");
        createBodies();
        rocketBody = rocket.getBody();//physicBodies.get(0);
    }

    private void createBodies() {

        for (Entity entity : entities) {

            entity.createBody(world);

            /*Body body;
            if (entity instanceof Asteroid)
                body = createBody(entity, entity + "_" + ((Asteroid) entity).getId(), entity.getPosition());
            else if (entity instanceof Ground)
                body = createBody(entity, entity + "_" + id, entity.getPosition());
            else
                body = createBody(entity, entity.toString(), entity.getPosition());
            physicBodies.add(body);*/
        }
    }

    private Body createBody(Entity entity, String name, Vector2 position) {
        Body body = physicsShapeCache.createBody(name, world, SCALE, SCALE);
        body.setUserData(entity);
        body.setTransform(position.x, position.y, 0);
        body.setFixedRotation(true);
        if (!(entity instanceof Rocket)) body.setType(BodyDef.BodyType.StaticBody);
        return body;
    }

    public void stepWorld() {

        if (rocketBody.getLinearVelocity().equals(new Vector2(0, 0))) rocket.setState(IDLE);

        float delta = Gdx.graphics.getDeltaTime();

        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }

        awakeNearAsteroid();
        checkBonuses();
        if (checkDistance) disableLastPlatform();
        rocket.setAngle();
        rocket.updateFuel();
    }

    private void awakeNearAsteroid() {

        for (Entity asteroid : entities) {
            if (asteroid instanceof Asteroid) {
                int i = entities.indexOf(asteroid);
                Body asteroidBody = asteroid.getBody();//physicBodies.get(i);
                boolean isDynamic = ((Asteroid) asteroid).isDynamic();

                Vector2 position = asteroidBody.getPosition();
                if (position.dst(rocketBody.getPosition()) < 5 && isDynamic) {
                    if (position.y < rocketBody.getPosition().y) {
                        if (!(asteroidBody.getType() == BodyDef.BodyType.StaticBody)) {
                            //TODO some shaking before falling?
                        }
                    } else {
                        asteroidBody.setType(BodyDef.BodyType.DynamicBody);
                    }
                }

            }
        }
    }

    private void disableLastPlatform() {

        Platform platform = player.getCurrentPlatform();
        int index = entities.indexOf(platform);
        Body body = platform.getBody();//physicBodies.get(index);

        //Gdx.app.log("next entity", String.valueOf(entities.get(index + 1)));

        if (body.getPosition().dst(rocketBody.getPosition()) > 5 &&
                index + 1 < entities.size() && entities.get(index + 1) instanceof Platform) {
            platform.setState(POP);
            checkDistance = false;
        }
    }

    public void respawn() {

        //TODO return rocketBody on platform with index current + 1

        Vector2 position;

        Gdx.app.log("boolean", String.valueOf(entities.contains(player.getCurrentPlatform())));

        if (entities.contains(player.getCurrentPlatform())) {
            position = entities.get(currentPlatformIndex + 1).getPosition();
        } else {
            position = entities.get(currentPlatformIndex).getPosition();
        }

        rocket.setState(IDLE);
        rocket.setAngle(0);

        rocketBody.setTransform(position.x + 28.5f * SCALE, position.y + 25f * SCALE, 0);
    }

    private void checkBonuses() {
        Map<Bonus.Type, Boolean> currentBonuses = player.getCurrentBonuses();
        for (Bonus.Type type : currentBonuses.keySet()) {
            if (currentBonuses.get(type)) {
                switch (type) {
                    case FUEL:
                        rocket.changeDefaultFuelConsumption();
                }
            } else {
                switch (type) {
                    case FUEL:
                        rocket.setDefaultFuelConsumption();
                }
            }
        }
    }

    public void contactProcess(Contact contact) {

        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        Gdx.app.log("contact", "between " + bodyA.getUserData() + " and " + bodyB.getUserData());

        if (bodyA.getUserData() instanceof Rocket || bodyA.getUserData() instanceof Rocket) {

            if (!(bodyA.getUserData() instanceof Rocket)) {
                Body s = bodyB;
                bodyB = bodyA;
                bodyA = s;
            }

            switch (bodyB.getUserData().getClass().getSimpleName()) {

                case "Platform":

                    if (bodyA.getPosition().y >= bodyB.getPosition().y + 10f * SCALE &&
                            abs(bodyA.getLinearVelocity().y) < 10 &&
                            abs(bodyA.getLinearVelocity().x) < 10
                    ) {
                        Platform platform = (Platform) bodyB.getUserData();
                        int i = entities.indexOf(platform);

                        if (platform.getFuel()) {
                            rocket.setRefueling();
                            if (platform.getId() > 0) player.addScore(100);
                            player.setCurrentPlatform(platform);
                            currentPlatformIndex = entities.indexOf(platform);
                            platform.refuel();
                        }
                        //TODO check this
                        if (i + 1 >= entities.size() || !(entities.get(i + 1) instanceof Platform))
                            rocket.setState(POP);
                    } else rocket.setState(Entity.State.POP);
                    break;

                case "Asteroid":
                case "Ground":
                    rocket.setState(Rocket.State.POP);
                    break;
                case "Bonus":
                    ((Bonus) bodyB.getUserData()).setState(Entity.State.POP);
                    break;
                case "Coin":
                    Coin coin = (Coin) bodyB.getUserData();
                    if (coin.getState() != POP) player.changeBalance(1);
                    coin.setState(Entity.State.POP);
                    break;
            }
        } else if (bodyA.getUserData() instanceof Asteroid || bodyB.getUserData() instanceof Asteroid) {
            if (bodyB.getUserData() instanceof Asteroid) {
                Body t = bodyB;
                bodyB = bodyA;
                bodyA = t;
            }
            if (!(bodyB.getUserData() instanceof Bonus) && !(bodyB.getUserData() instanceof Coin)) {
                Asteroid asteroid = (Asteroid) bodyA.getUserData();
                asteroid.setState(Entity.State.POP);
            }
        }
    }

    public void endContactProcess(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        Gdx.app.log("endContact", "between " + bodyA.getUserData() + " and " + bodyB.getUserData());

        if (bodyA.getUserData() instanceof Platform && bodyB.getUserData() instanceof Rocket ||
                bodyA.getUserData() instanceof Rocket && bodyB.getUserData() instanceof Platform) checkDistance = true;
    }

    public MyWorld(List<Entity> addEntities, int id) {

        Platform.resetIdCounter();

        this.player = new Player();
        entities.addAll(addEntities);
        this.rocket = (Rocket) entities.get(0);

        this.id = id;

        create();
    }

    public Player getPlayer() {
        return player;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public List<? extends Entity> getEntities() {
        return entities;
    }

    public World getWorld() {
        return world;
    }

    public List<Entity> getEntitiesForRemove() {
        return entitiesForRemove;
    }

    public int getId() {
        return id;
    }

    public int getCurrentPlatformIndex() {
        return currentPlatformIndex;
    }

    /*@Override
    public MyWorld clone() throws CloneNotSupportedException {

        MyWorld newWorld = (MyWorld) super.clone();

        newWorld.getEntities().clear();
        for (Entity entity : this.getEntities()) {
            newWorld.getEntities().add( (Rocket) entity.clone());
        }

        return newWorld;
    }*/
}
