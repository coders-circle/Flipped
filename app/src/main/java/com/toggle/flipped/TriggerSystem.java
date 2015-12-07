package com.toggle.flipped;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.ContactListener;
import com.toggle.katana2d.physics.PhysicsBody;

public class TriggerSystem extends com.toggle.katana2d.System implements ContactListener {

    public TriggerSystem() {
        super(new Class[]{Trigger.class, PhysicsBody.class});
    }

    @Override
    public void onEntityAdded(Entity entity) {
        Trigger t = entity.get(Trigger.class);
        PhysicsBody b = entity.get(PhysicsBody.class);
        if (t.type == Trigger.Type.BUTTON) {
            b.contactListener = this;
        }
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            Trigger trigger = entity.get(Trigger.class);
            PhysicsBody b = entity.get(PhysicsBody.class);

            if (trigger.type == Trigger.Type.LEVER) {
                float angle = trigger.leverPosition * 30 + 60;

                //b.body.applyTorque(angle, false);
                b.body.setTransform(b.body.getPosition(), (float) Math.toRadians(angle));
            }
            else {
                if (!trigger.isPressed && trigger.load > 1) {
                    trigger.trigger();
                    trigger.isPressed = true;
                }
                else if (trigger.isPressed && trigger.load < 1) {
                    trigger.trigger();
                    trigger.isPressed = false;
                }
            }
        }
    }

    @Override
    public void beginContact(Contact contact, Fixture me, Fixture other) {
        WorldManifold manifold = contact.getWorldManifold();
        /*float dot = manifold.getNormal().dot(0, 1);
        if (dot == 0 || dot == 1) {*/
            Trigger m = ((Entity)me.getUserData()).get(Trigger.class);
            m.load += other.getBody().getMass();
        //}
    }

    @Override
    public void endContact(Contact contact, Fixture me, Fixture other) {
        WorldManifold manifold = contact.getWorldManifold();
        /*float dot = manifold.getNormal().dot(0, 1);
        if (dot == 0 || dot == 1) {*/
            Trigger m = ((Entity)me.getUserData()).get(Trigger.class);
            m.load -= other.getBody().getMass();
        //}
    }

    @Override
    public void preSolve(Contact contact, Fixture me, Fixture other) {

    }

    @Override
    public void postSolve(Contact contact, Fixture me, Fixture other) {

    }
}
