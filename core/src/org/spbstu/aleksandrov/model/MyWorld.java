package org.spbstu.aleksandrov.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import org.spbstu.aleksandrov.model.entities.*;
import org.spbstu.aleksandrov.view.Listener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static org.spbstu.aleksandrov.model.entities.Entity.State.IDLE;
import static org.spbstu.aleksandrov.model.entities.Entity.State.POP;

public class MyWorld {

    private final Player player;
    private final Rocket rocket;
    private final int id;
    private Body rocketBody;
    private World world;

    private final List<Entity> entities = new ArrayList<>();
    private final List<Body> physicBodies = new ArrayList<>();
    private final List<Entity> entitiesForRemove = new ArrayList<>();
    private boolean checkDistance = false;

    private PhysicsShapeCache physicsShapeCache;

    static final float STEP_TIME = 1f / 60f;
    static final int VELOCITY_ITERATIONS = 6;
    static final int POSITION_ITERATIONS = 2;
    public static final float SCALE = 0.05f;

    float accumulator = 0;

    private void create() {
        Box2D.init();
        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new Listener(this));
        physicsShapeCache = new PhysicsShapeCache("physicBodies.xml");
        createBodies();
        rocketBody = physicBodies.get(0);
    }

    private void createBodies() {

        for (Entity entity : entities) {
            Body body;
            if (entity instanceof Asteroid)
                body = createBody(entity, entity + "_" + ((Asteroid) entity).getId(), entity.getPosition());
            else if (entity instanceof Ground)
                body = createBody(entity, entity + "_" + id, entity.getPosition());
            else
                body = createBody(entity, entity.toString(), entity.getPosition());
            physicBodies.add(body);
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
        float delta = Gdx.graphics.getDeltaTime();

        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }

        awakeNearAsteroid();
        getCurrentPlatform();
        if (checkDistance) disableLastPlatform();
        rocket.setAngle();
        rocket.updateFuel();
        if (rocketBody.getLinearVelocity().equals(new Vector2(0, 0))) rocket.setState(IDLE);
    }

    private void awakeNearAsteroid() {

        for (Entity asteroid : entities) {
            if (asteroid instanceof Asteroid) {
                int i = entities.indexOf(asteroid);
                Body asteroidBody = physicBodies.get(i);
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
        Body body = physicBodies.get(index);

        if (body.getPosition().dst(rocketBody.getPosition()) > 5) {
            platform.setState(POP);
            checkDistance = false;
        }

    }


    private void getCurrentPlatform() {

        Platform currentPlatform = player.getCurrentPlatform();

        //TODO get first platform in entities
        if (currentPlatform == null) {
            player.setCurrentPlatform((Platform) entities.get(2));
            currentPlatform = player.getCurrentPlatform();
        }

        int index;

        //TODO
        for (index = 0/*entities.indexOf(currentPlatform)*/; index < entities.size(); index++) {

            if (entities.get(index) instanceof Platform) {

                Platform platform = (Platform) entities.get(index);

                if (!platform.getFuel() && platform.getId() > currentPlatform.getId())
                    player.setCurrentPlatform(platform);
            } else break;
        }
    }

    public void respawn() {

        //TODO return rocketBody on platform with index current + 1

        rocket.setState(IDLE);
        //Vector2 position = platformBody.getPosition();
        //rocketBody.setTransform(position, 0);

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

            int i = physicBodies.indexOf(bodyB);

            switch (bodyB.getUserData().getClass().getSimpleName()) {

                case "Platform":
                    if (bodyA.getPosition().y > bodyB.getPosition().y &&
                            abs(bodyA.getLinearVelocity().y) < 10 &&
                            abs(bodyA.getLinearVelocity().x) < 10
                    ) {
                        if (rocket.getState() != IDLE) {

                            Platform platform = (Platform) entities.get(i);

                            if (platform.getFuel()) {
                                rocket.setRefueling();
                                player.addScore(100);
                                player.setCurrentPlatform(platform);
                                platform.refuel();
                            }
                        }
                    } else rocket.setState(Entity.State.POP);
                    break;

                case "Asteroid":
                case "Ground":
                    rocket.setState(Rocket.State.POP);
                    break;
                case "Bonus":
                    entities.get(i).setState(Entity.State.POP);
                    break;

                case "Coin":
                    entities.get(i).setState(Entity.State.POP);
                    player.increaseBalance();
                    break;
            }
        } else if (bodyA.getUserData() instanceof Asteroid || bodyB.getUserData() instanceof Asteroid) {
            if (bodyA.getUserData() instanceof Asteroid) bodyB = bodyA;
            int i = physicBodies.indexOf(bodyB);
            Asteroid asteroid = (Asteroid) entities.get(i);
            asteroid.setState(Entity.State.POP);
        }
    }

    public void endContactProcess(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        Gdx.app.log("endContact", "between " + bodyA.getUserData() + " and " + bodyB.getUserData());

        if (bodyA.getUserData() instanceof Platform || bodyB.getUserData() instanceof Platform) checkDistance = true;
    }

    public MyWorld(Player player, List<Entity> addEntities, int id) {

        this.player = player;
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

    public List<Body> getPhysicBodies() {
        return physicBodies;
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
}
