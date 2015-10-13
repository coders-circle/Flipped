package com.toggle.katana2d.physics;

import com.toggle.katana2d.Component;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class PhysicsBody implements Component {
    public PhysicsBody(Body body)
    {
        this.body = body;
    }

    // For default use: friction=0.2, restitution=0, density = 0(static)
    public PhysicsBody(BodyType type, float posX, float posY, float angle,
                       PolygonShape shape, float density, float friction, float restitution,
                       boolean bullet, Object object)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position = new Vec2(posX, posY);
        bodyDef.angle = angle;
        bodyDef.bullet = bullet;
        bodyDef.userData = object;
        body = PhysicsSystem.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
    }

    public Body body;
}
