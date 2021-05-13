package org.spbstu.aleksandrov.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import org.spbstu.aleksandrov.model.entities.*;
import org.spbstu.aleksandrov.view.Listener;

import java.util.ArrayList;
import java.util.List;

import static org.spbstu.aleksandrov.model.entities.Entity.State.IDLE;

public class MyWorld {

    public Player player;
    public Rocket rocket;
    public Body rocketBody;

    public List<? extends Entity> entities = new ArrayList<>();
    public List<Body> physicBodies = new ArrayList<>();

    PhysicsShapeCache physicsShapeCache;

    static final float STEP_TIME = 1f / 60f;
    static final int VELOCITY_ITERATIONS = 6;
    static final int POSITION_ITERATIONS = 2;
    public static final float SCALE = 0.05f;

    float accumulator = 0;

    public World world;

    public void create() {
        Box2D.init();
        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new Listener(this));
        physicsShapeCache = new PhysicsShapeCache("physicBodies.xml");
        createBodies();
        rocket = (Rocket) entities.get(0);
        rocketBody = physicBodies.get(0);
    }

    private void createBodies() {

        for (Entity entity : entities) {
            Body body = createBody(entity, entity.toString(), entity.getPosition());
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

    public World getWorld() {
        return world;
    }


    public boolean delete = false;
    public void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();

        accumulator += Math.min(delta, 0.25f);

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        }

        awakeNearAsteroid();
        ((Rocket) entities.get(0)).setAngle();
        if (rocketBody.getLinearVelocity().equals(new Vector2(0, 0))) rocket.setState(IDLE);
    }

    private void awakeNearAsteroid() {

        for (Entity asteroid : entities) {
            if (asteroid instanceof Asteroid) {
                    int i = entities.indexOf(asteroid);
                    Body asteroidBody = physicBodies.get(i);
                    boolean isDynamic = ((Asteroid) asteroid).isDynamic();

                    Vector2 position = asteroidBody.getPosition();
                    if (position.dst(physicBodies.get(0).getPosition()) < 20 && isDynamic) {
                        if (position.y < physicBodies.get(0).getPosition().y) {
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

    private void getCurrentPlatform () {
        for (Entity platform : entities) {
            if (platform instanceof Platform) {

                int i = entities.indexOf(platform);
                Body body = physicBodies.get(i);
                float lastDistance = body.getPosition().dst(physicBodies.get(0).getPosition());

                if (body.getPosition().dst(physicBodies.get(0).getPosition()) < lastDistance &&
                        body.getPosition().y > physicBodies.get(0).getPosition().y) {
                    player.setCurrentPlatform(((Platform) platform).getId());
                }
            //TODO
            }
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

            if (!(bodyA.getUserData() instanceof Rocket)) bodyB = bodyA;

            int i = physicBodies.indexOf(bodyB);
            switch (bodyB.getUserData().toString()) {

                case ("platform") :
                    if (bodyA.getPosition().y > bodyB.getPosition().y/* && speed check*/) {

                        if (rocket.getState() != IDLE) {
                            //platforms.get(0).setState(Entity.State.POP);

                            Platform platform = (Platform) entities.get(i);

                            player.addScore(100);
                            if (platform.fuel) rocket.setFullFuel();
                            platform.refuel();
                        }
                    } else rocket.setState(Entity.State.POP);
                    break;

                case ("asteroid") :
                case ("ground") : rocket.setState(Rocket.State.POP);
                     break;
                case ("bonus") :
                    entities.get(i).setState(Entity.State.POP);
                    deleteBody(bodyB);
                    break;

                case ("coin") :
                    entities.get(i).setState(Entity.State.POP);
                    deleteBody(bodyB);
                    player.increaseBalance();
                    break;
            }
        }
        else if (bodyA.getUserData() instanceof Asteroid || bodyB.getUserData() instanceof Asteroid) {
            if (bodyA.getUserData() instanceof Asteroid) bodyB = bodyA;
            int i = physicBodies.indexOf(bodyB);
            Asteroid asteroid  = (Asteroid) entities.get(i);
            asteroid.setState(Entity.State.POP);
            asteroid.setPosition(bodyB.getPosition());
            if (bodyA.getUserData() instanceof Asteroid) deleteBody(bodyA);
            else deleteBody(bodyB);
        }
    }

    public void endContactProcess(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB =  contact.getFixtureB().getBody();

        Gdx.app.log("endContact", "between " + bodyA.getUserData() + " and " + bodyB.getUserData());

        //rocket.getState() != Rocket.State.IDLE && bodyB.getPosition().dst(rocketBody.getPosition()) > 10

        Entity platform;
        if (bodyA.getUserData() instanceof Platform) platform = (Entity) bodyA.getUserData();
        else platform = (Entity) bodyB.getUserData();

        if (platform instanceof Platform) platform.setState(Entity.State.POP);
    }

    private void deleteBody(Body body) {
        //It's more correct to store and destroy bodies right after the myWorld.stepWorld()
        //in GameScreen but but for some reason this does not work.
        Gdx.app.postRunnable(() -> world.destroyBody(body));
    }

    public MyWorld(Player player, List<? extends Entity> entities) {

        this.player = player;
        this.entities = entities;
        this.rocket = (Rocket) entities.get(0);

        create();
    }
}
