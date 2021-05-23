package org.spbstu.aleksandrov.model.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import static org.spbstu.aleksandrov.model.MyWorld.SCALE;

public abstract class Entity {

    public enum State {
        IDLE, POP, FLYING_UP, FLYING_RIGHT, FLYING_LEFT
    }

    Body body;
    private Vector2 position;
    State state = State.IDLE;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Body getBody() {
        return body;
    }

    public Entity(Vector2 position) {
        this.position = position;
    }

    public Entity() {
        this.position = new Vector2(0, 0);
    }

    public void createBody(World world) {
        PhysicsShapeCache physicsShapeCache = new PhysicsShapeCache("physicbodies.xml");
        this.body = physicsShapeCache.createBody(this.toString(), world, SCALE, SCALE);
        this.body.setUserData(this);
        this.body.setTransform(position.x, position.y, 0);
        this.body.setFixedRotation(true);
        body.setType(BodyDef.BodyType.StaticBody);
    }

    /*@Override
    public Entity clone() throws CloneNotSupportedException {
        Entity newEntity = (Entity) super.clone();
        newEntity.setState(this.getState());
        newEntity.setPosition(this.getPosition());
        return newEntity;
    }*/
}
