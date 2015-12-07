package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.ContactListener;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

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

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(20 * PhysicsSystem.METERS_PER_PIXEL, 4 * PhysicsSystem.METERS_PER_PIXEL,
                    new Vector2(0, -18 * PhysicsSystem.METERS_PER_PIXEL), 0);
            b.createSensor(shape);
            t.originalPosition = new Vector2(b.body.getPosition());
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
                if (trigger.load > 0.4f) {
                    if (trigger.displacement >= 10) {
                        trigger.displacement = 10;
                        if (!trigger.isPressed) {
                            trigger.trigger();
                            trigger.isPressed = true;
                        }
                    } else
                        trigger.displacement += dt*10;
                }
                else if (trigger.load < 0.4f) {
                    if (trigger.displacement <= 0) {
                        trigger.displacement = 0;
                        if (trigger.isPressed) {
                            trigger.trigger();
                            trigger.isPressed = false;
                        }
                    }
                    else
                        trigger.displacement -= dt*10;
                }

                b.body.setTransform(trigger.originalPosition.x, trigger.originalPosition.y+trigger.displacement*PhysicsSystem.METERS_PER_PIXEL, b.body.getAngle());
            }
        }
    }

    @Override
    public void beginContact(Contact contact, Fixture me, Fixture other) {
        if (me.isSensor()) {
            Trigger m = ((Entity)me.getUserData()).get(Trigger.class);
            m.load += other.getBody().getMass();
            m.loads.add(other);
        }
    }

    @Override
    public void endContact(Contact contact, Fixture me, Fixture other) {
        if (me.isSensor()) {
            Trigger m = ((Entity) me.getUserData()).get(Trigger.class);
            if (m.loads.contains(other)) {
                m.load -= other.getBody().getMass();
                m.loads.remove(other);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Fixture me, Fixture other) {

    }

    @Override
    public void postSolve(Contact contact, Fixture me, Fixture other) {

    }
}
