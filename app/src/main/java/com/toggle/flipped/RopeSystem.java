package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

import java.util.List;

public class RopeSystem extends com.toggle.katana2d.System {

    private World mWorld;
    private GLRenderer mRenderer;
    public RopeSystem(World world, GLRenderer renderer) {
        super(new Class[]{Rope.class});
        mWorld = world;
        mRenderer = renderer;
    }

    @Override
    public void onEntityAdded(Entity entity) {
        Rope rope = entity.get(Rope.class);

        float w = rope.thickness * PhysicsSystem.METERS_PER_PIXEL;
        float h = rope.segmentLength * PhysicsSystem.METERS_PER_PIXEL;

        Body startBody = null, endBody = null;
        if (rope.startBody.has(PhysicsBody.class))
            startBody = rope.startBody.get(PhysicsBody.class).body;
        if (rope.endBody != null && rope.endBody.has(PhysicsBody.class))
            endBody = rope.endBody.get(PhysicsBody.class).body;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w/2, h/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 6f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.2f;
        fixtureDef.shape = shape;
        //fixtureDef.userData = entity;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        //bodyDef.userData = entity;

        RevoluteJointDef rjointDef = new RevoluteJointDef();
        DistanceJointDef djointDef = new DistanceJointDef();
        rjointDef.collideConnected = true;
        djointDef.collideConnected = true;

        List<Vector2> segmentsPath = Utilities.getPoints(rope.path, rope.segmentLength, true);
        rope.numSegments = segmentsPath.size() - 1;

        Body link = startBody;
        float lastY = segmentsPath.get(0).y; float lastX = segmentsPath.get(0).x;
        for (int i=0; i<rope.numSegments; ++i) {
            Vector2 p0 = segmentsPath.get(i);
            Vector2 p1 = segmentsPath.get(i + 1);

            Vector2 center = Utilities.getCenter(p0, p1);
            bodyDef.position.x = center.x;
            bodyDef.position.y = center.y;
            bodyDef.angle = (float)Math.atan2(p1.y-p0.y, p1.x-p0.x);

            Body body = mWorld.createBody(bodyDef);
            Fixture f = body.createFixture(fixtureDef);
            f.setUserData(entity);
            body.setUserData(entity);
            body.setGravityScale(0.2f); // less gravity action

            rope.segments.add(body);

            if (link != null) {
                rjointDef.initialize(link, body, new Vector2(lastX, lastY));
                mWorld.createJoint(rjointDef);

                djointDef.initialize(link, body, link.getWorldCenter(), body.getWorldCenter());
                mWorld.createJoint(djointDef);
            }

            link = body;
            lastY = segmentsPath.get(i + 1).y;
            lastX = segmentsPath.get(i + 1).x;
        }

        if (endBody != null && link != null) {
            rjointDef.initialize(link, endBody, link.getWorldCenter());
            mWorld.createJoint(rjointDef);

            djointDef.initialize(link, endBody, link.getWorldCenter(), endBody.getWorldCenter());
            mWorld.createJoint(djointDef);
        }
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            Rope rope = entity.get(Rope.class);

            // if burning, we need to animate the burning segment
            if (rope.isBurning) {
                rope.burnData.burningSegmentSprite.animate(dt);

                if (rope.segments.size() > 0) {
                    Rope.BurnData bd = rope.burnData;

                    // Increase time passed
                    bd.timePassed += dt;

                    // If enough time has passed, burn a segment
                    if (bd.timePassed > bd.timeToBurn) {
                        // first remove the segment
                        rope.removeSegment(rope.segments.size() - 1);

                        bd.timePassed = 0; // -= bd.timeToBurn;

                        // reset the burning segment sprite
                        bd.burningSegmentSprite.reset();
                    }
                }
            }
        }
    }

    // TODO: Use temporal antialiasing

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
                    rope.burnData.burningSegmentSprite.draw(mRenderer, x, y, angle);
                else
                    rope.segmentSprite.draw(mRenderer, x, y, angle);
            }
        }
    }
}
