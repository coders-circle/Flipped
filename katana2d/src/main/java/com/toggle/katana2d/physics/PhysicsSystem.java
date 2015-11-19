package com.toggle.katana2d.physics;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.System;
import com.toggle.katana2d.Transformation;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.Iterator;

// Uses box2d to update all entities with PhysicsBody and Transformation components
public class PhysicsSystem extends System implements ContactListener {
    public static final float PIXELS_PER_METER = 32f;               // 32 pixels = 1 meter
    public static final float METERS_PER_PIXEL = 1 / PIXELS_PER_METER;

    private World world = new World(new Vec2(0, 10));

    public PhysicsSystem()
    {
        super(new Class[]{PhysicsBody.class, Transformation.class});
        world.setContactListener(this);
    }

    public World getWorld() { return world; }

    @Override
    public void update(float dt) {
        world.step(dt, 10, 8);

        for (Entity entity: mEntities) {
            Transformation t = entity.get(Transformation.class);
            PhysicsBody b = entity.get(PhysicsBody.class);

            t.saveState();

            t.x = b.body.getPosition().x * PIXELS_PER_METER;
            t.y = b.body.getPosition().y * PIXELS_PER_METER;
            t.angle = (float)Math.toDegrees(b.body.getAngle());
        }
    }

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureA().getUserData() != null)
        if (contact.getFixtureA().getUserData().getClass() == Entity.class) {
            Entity e = (Entity)contact.getFixtureA().getUserData();
            if (e.has(PhysicsBody.class)) {
                PhysicsBody b = e.get(PhysicsBody.class);
                if (b.contactListener != null)
                    b.contactListener.beginContact(contact, contact.getFixtureA(), contact.getFixtureB());
            }
        }

        if (contact.getFixtureB().getUserData() != null)
        if (contact.getFixtureB().getUserData().getClass() == Entity.class) {
            Entity e = (Entity)contact.getFixtureB().getUserData();
            if (e.has(PhysicsBody.class)) {
                PhysicsBody b = e.get(PhysicsBody.class);
                if (b.contactListener != null)
                    b.contactListener.beginContact(contact, contact.getFixtureB(), contact.getFixtureA());
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getUserData() != null)
        if (contact.getFixtureA().getUserData().getClass() == Entity.class) {
            Entity e = (Entity)contact.getFixtureA().getUserData();
            if (e.has(PhysicsBody.class)) {
                PhysicsBody b = e.get(PhysicsBody.class);
                if (b.contactListener != null)
                    b.contactListener.endContact(contact, contact.getFixtureA(), contact.getFixtureB());
            }
        }

        if (contact.getFixtureB().getUserData() != null)
        if (contact.getFixtureB().getUserData().getClass() == Entity.class) {
            Entity e = (Entity)contact.getFixtureB().getUserData();
            if (e.has(PhysicsBody.class)) {
                PhysicsBody b = e.get(PhysicsBody.class);
                if (b.contactListener != null)
                    b.contactListener.endContact(contact, contact.getFixtureB(), contact.getFixtureA());
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        if (contact.getFixtureA().getUserData() != null)
            if (contact.getFixtureA().getUserData().getClass() == Entity.class) {
                Entity e = (Entity)contact.getFixtureA().getUserData();
                if (e.has(PhysicsBody.class)) {
                    PhysicsBody b = e.get(PhysicsBody.class);
                    if (b.contactListener != null)
                        b.contactListener.preSolve(contact, contact.getFixtureA(), contact.getFixtureB());
                }
            }

        if (contact.getFixtureB().getUserData() != null)
            if (contact.getFixtureB().getUserData().getClass() == Entity.class) {
                Entity e = (Entity)contact.getFixtureB().getUserData();
                if (e.has(PhysicsBody.class)) {
                    PhysicsBody b = e.get(PhysicsBody.class);
                    if (b.contactListener != null)
                        b.contactListener.preSolve(contact, contact.getFixtureB(), contact.getFixtureA());
                }
            }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        if (contact.getFixtureA().getUserData() != null)
            if (contact.getFixtureA().getUserData().getClass() == Entity.class) {
                Entity e = (Entity)contact.getFixtureA().getUserData();
                if (e.has(PhysicsBody.class)) {
                    PhysicsBody b = e.get(PhysicsBody.class);
                    if (b.contactListener != null)
                        b.contactListener.postSolve(contact, contact.getFixtureA(), contact.getFixtureB());
                }
            }

        if (contact.getFixtureB().getUserData() != null)
            if (contact.getFixtureB().getUserData().getClass() == Entity.class) {
                Entity e = (Entity)contact.getFixtureB().getUserData();
                if (e.has(PhysicsBody.class)) {
                    PhysicsBody b = e.get(PhysicsBody.class);
                    if (b.contactListener != null)
                        b.contactListener.postSolve(contact, contact.getFixtureB(), contact.getFixtureA());
                }
            }
    }
}
