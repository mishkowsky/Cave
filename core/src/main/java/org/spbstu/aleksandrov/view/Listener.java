package org.spbstu.aleksandrov.view;

import com.badlogic.gdx.physics.box2d.*;
import org.spbstu.aleksandrov.model.MyWorld;

public class Listener implements ContactListener {

    private final MyWorld myWorld;

    public Listener(MyWorld myWorld) {
        this.myWorld = myWorld;
    }

    @Override
    public void beginContact(Contact contact) {
        myWorld.contactProcess(contact);
    }

    @Override
    public void endContact(Contact contact) {
        myWorld.endContactProcess(contact);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
