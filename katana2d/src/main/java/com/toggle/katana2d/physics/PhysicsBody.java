package com.toggle.katana2d.physics;

import com.toggle.katana2d.Component;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

// PhysicsBody component that represents a box-2d body
// with mass, friction etc.
public class PhysicsBody implements Component {
    public PhysicsBody(Body body) {
        this.body = body;
    }

    private void init(World world, BodyType type, float posX, float posY, float angle,
                      PolygonShape shape, float density, float friction, float restitution,
                      boolean bullet, Object object) {
        posX = posX * PhysicsSystem.WORLD_TO_BOX;
        posY = posY * PhysicsSystem.WORLD_TO_BOX;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position = new Vec2(posX, posY);
        bodyDef.angle = (float)Math.toRadians(angle);
        bodyDef.bullet = bullet;
        bodyDef.userData = object;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
    }

    // Make sure entity has sprite and transformation
    public PhysicsBody(World world, BodyType type, Entity entity, float density, float friction, float restitution,
                       boolean bullet) {
        Transformation t = entity.get(Transformation.class);
        Sprite s = entity.get(Sprite.class);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(s.glSprite.width/2 * PhysicsSystem.WORLD_TO_BOX - 0.01f, s.glSprite.height/2 * PhysicsSystem.WORLD_TO_BOX - 0.01f);

        init(world, type, t.x, t.y, t.angle, shape, density, friction, restitution, bullet, entity);
    }

    // Make sure entity has transformation
    public PhysicsBody(World world, BodyType type, Entity entity, PolygonShape shape,
                       float density, float friction, float restitution, boolean bullet) {
        Transformation t = entity.get(Transformation.class);
        init(world, type, t.x, t.y, t.angle, shape, density, friction, restitution, bullet, entity);
    }

    // For default use: friction=0.2, restitution=0, density = 0(static)
    public PhysicsBody(World world, BodyType type, float posX, float posY, float angle,
                       PolygonShape shape, float density, float friction, float restitution,
                       boolean bullet, Object object) {
        init(world, type, posX, posY, angle, shape, density, friction, restitution, bullet, object);
    }

    public Body body;
}
