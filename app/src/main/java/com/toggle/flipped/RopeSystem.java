package com.toggle.flipped;


import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsSystem;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public class RopeSystem extends com.toggle.katana2d.System {

    private World mWorld;
    public RopeSystem(World world) {
        super(new Class[]{Rope.class});
        mWorld = world;
    }

    @Override
    public void onEntityAdded(Entity entity) {
        Rope rope = entity.get(Rope.class);

        float w = rope.thickness * PhysicsSystem.METERS_PER_PIXEL;
        float h = rope.segmentLength * PhysicsSystem.METERS_PER_PIXEL;
        float x = rope.initX * PhysicsSystem.METERS_PER_PIXEL;
        float y = rope.initY * PhysicsSystem.METERS_PER_PIXEL;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w/2, h/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 2;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.2f;
        fixtureDef.shape = shape;
        fixtureDef.userData = entity;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.x = x;
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.userData = entity;

        RevoluteJointDef jointDef = new RevoluteJointDef();

        Body link = rope.startBody;
        float lastY = y;
        for (int i=0; i<rope.numSegments; ++i) {
            bodyDef.position.y = y + h/2 + h * i;

            Body body = mWorld.createBody(bodyDef);
            body.createFixture(fixtureDef);

            rope.segments.add(body);

            jointDef.initialize(link, body, new Vec2(x, lastY));
            mWorld.createJoint(jointDef);

            link = body;
            lastY = bodyDef.position.y;
        }

        if (rope.endBody != null) {
            jointDef.initialize(link, rope.endBody, link.getWorldCenter());
            mWorld.createJoint(jointDef);
        }
    }

    @Override
    public void update(double dt) {
        for (Entity entity: mEntities) {
            Rope rope = entity.get(Rope.class);

            // if burning, we need to animate the burning segment
            if (rope.isBurning)
                rope.burnData.burningSegmentSprite.animate(dt);

            if (rope.segments.size() > 0) {
                if (rope.isBurning) {
                    Rope.BurnData bd = rope.burnData;

                    // Increase time passed
                    bd.timePassed += dt;

                    // If enough time has passed, burn a segment
                    if (bd.timePassed > bd.timeToBurn) {
                        // first remove the segment
                        Body body = rope.segments.get(rope.segments.size()-1);
                        mWorld.destroyBody(body);
                        rope.segments.remove(body);

                        bd.timePassed = 0; // -= bd.timeToBurn;

                        // reset the burning segment sprite
                        bd.burningSegmentSprite.reset();
                    }
                }
            }
        }
    }

    @Override
    public void draw() {
        for (Entity entity: mEntities) {
            Rope rope = entity.get(Rope.class);
            if (rope.segmentSprite == null)
                continue;

            for (int i=0; i<rope.segments.size(); ++i) {
                Body body = rope.segments.get(i);
                float x = body.getPosition().x * PhysicsSystem.PIXELS_PER_METER;
                float y = body.getPosition().y * PhysicsSystem.PIXELS_PER_METER;
                float angle = (float)Math.toDegrees(body.getAngle());

                // if burning, show burning sprite for the last segment
                if (rope.isBurning && i == rope.segments.size()-1)
                    rope.burnData.burningSegmentSprite.draw(x, y, angle);
                else
                    rope.segmentSprite.draw(x, y, angle);
            }
        }
    }
}
