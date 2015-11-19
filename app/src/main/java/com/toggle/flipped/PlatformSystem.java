package com.toggle.flipped;

import com.toggle.katana2d.Component;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.physics.ContactListener;
import com.toggle.katana2d.physics.PhysicsBody;

import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

public class PlatformSystem extends com.toggle.katana2d.System implements ContactListener {

    public static class OneWayPlatform implements Component {
    }

    public PlatformSystem() {
        super(new Class[]{ OneWayPlatform.class, PhysicsBody.class});
    }

    @Override
    public void onEntityAdded(Entity entity) {
        PhysicsBody b = entity.get(PhysicsBody.class);
        b.contactListener = this;
    }

    @Override
    public void beginContact(Contact contact, Fixture me, Fixture other) {
    }

    @Override
    public void endContact(Contact contact, Fixture me, Fixture other) {
    }

    @Override
    public void preSolve(Contact contact, Fixture me, Fixture other) {
        if (other.getBody().getType() != BodyType.DYNAMIC)
            return;

        int numPoints = contact.getManifold().pointCount;
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);

        for (int i=0; i<numPoints; ++i)
            if (other.getBody().getLinearVelocityFromWorldPoint(worldManifold.points[i]).y >= 0)
                return;

        // If all contact points are going up, disable the contact
        contact.setEnabled(false);
    }

    @Override
    public void postSolve(Contact contact, Fixture me, Fixture other) {
    }
}
