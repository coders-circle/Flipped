package com.toggle.katana2d.physics;

import com.toggle.katana2d.Component;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

// PhysicsBody component that represents a box-2d body
// with mass, friction etc.
public class PhysicsBody implements Component {

    public static class Properties {
        // For default use: friction=0.2, restitution=0, density = 0(static)
        float density, friction, restitution;
        boolean bullet, fixedRotation;
        boolean sensor;

        public Properties(float density, float friction, float restitution, boolean bullet, boolean fixedRotation) {
            this.density = density; this.friction = friction; this.restitution = restitution;
            this.bullet = bullet; this.fixedRotation = fixedRotation;
            this.sensor = false;
        }

        public Properties(float density) {
            this(density, 0.2f, 0.0f, false, false);
        }

        public Properties(float density, float friction, float restitution) {
            this(density, friction, restitution, false, false);
        }

        public Properties(boolean sensor, boolean bullet, boolean fixedRotation) {
            this.sensor = sensor;
            this.bullet = bullet;
            this.fixedRotation = fixedRotation;
        }

        public Properties(boolean sensor) {
            this(sensor, false, false);
        }
    }

    public PhysicsBody(Body body) {
        this.body = body;
    }

    private void init(World world, BodyType type, float posX, float posY, float angle,
                      Shape shape, Object object, Properties properties) {
        posX = posX * PhysicsSystem.METERS_PER_PIXEL;
        posY = posY * PhysicsSystem.METERS_PER_PIXEL;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position = new Vec2(posX, posY);
        bodyDef.angle = (float)Math.toRadians(angle);
        bodyDef.userData = object;
        if (properties != null) {
            bodyDef.bullet = properties.bullet;
            bodyDef.fixedRotation = properties.fixedRotation;
        }
        body = world.createBody(bodyDef);

        if (properties != null) {
            FixtureDef fixtureDef = new FixtureDef();
            if (properties.sensor)
                fixtureDef.isSensor = true;
            else {
                fixtureDef.density = properties.density;
                fixtureDef.friction = properties.friction;
                fixtureDef.restitution = properties.restitution;
            }
            fixtureDef.shape = shape;
            fixtureDef.userData = object;
            body.createFixture(fixtureDef);
        }
    }

    // Make sure entity has sprite and transformation
    public PhysicsBody(World world, BodyType type, Entity entity, Properties properties) {
        Transformation t = entity.get(Transformation.class);
        Sprite s = entity.get(Sprite.class);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(s.texture.width/2 * PhysicsSystem.METERS_PER_PIXEL - 0.01f, s.texture.height/2 * PhysicsSystem.METERS_PER_PIXEL - 0.01f);

        init(world, type, t.x, t.y, t.angle, shape, entity, properties);
    }

    // Make sure entity has transformation
    public PhysicsBody(World world, BodyType type, Entity entity, Shape shape, Properties properties) {
        Transformation t = entity.get(Transformation.class);
        init(world, type, t.x, t.y, t.angle, shape, entity, properties);
    }

    // Make sure entity has transformation
    public PhysicsBody(World world, BodyType type, Entity entity, float width, float height, Properties properties) {
        Transformation t = entity.get(Transformation.class);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2 * PhysicsSystem.METERS_PER_PIXEL - 0.01f, height/2 * PhysicsSystem.METERS_PER_PIXEL - 0.01f);

        init(world, type, t.x, t.y, t.angle, shape, entity, properties);
    }

    public PhysicsBody(World world, BodyType type, float posX, float posY, float angle, Shape shape, Object object, Properties properties) {
        init(world, type, posX, posY, angle, shape, object, properties);
    }

    public Fixture createSensor(Shape shape) {
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.userData = body.getUserData();
        return body.createFixture(fdef);
    }

    public Body body;
    //public List<Collision> collisions = new ArrayList<>();
    public ContactListener contactListener;

    /*public static class Collision {
        public Fixture otherFixture;
        public Fixture myFixture;
    }*/
}
