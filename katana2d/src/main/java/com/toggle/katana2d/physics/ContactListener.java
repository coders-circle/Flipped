package com.toggle.katana2d.physics;

import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

public interface ContactListener {
    /*boolean beginContact(Contact contact, boolean isFirstFixture);
    void endContact(Contact contact, boolean isFirstFixture);*/

    void beginContact(Contact contact, Fixture me, Fixture other);

    void endContact(Contact contact, Fixture me, Fixture other);

    void preSolve(Contact contact, Fixture me, Fixture other);

    void postSolve(Contact contact, Fixture me, Fixture other);
}
