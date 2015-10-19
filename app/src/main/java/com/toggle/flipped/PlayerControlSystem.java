package com.toggle.flipped;

import android.util.Log;

import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

public class PlayerControlSystem extends com.toggle.katana2d.System {
    public static class Player implements Component {
        int moveDirection;
        Fixture groundFixture;
    }

    private Game mGame;

    public PlayerControlSystem(Game game) {
        super(new Class[] { Player.class, Transformation.class, PhysicsBody.class });
        mGame = game;
    }

    @Override
    public void onEntityAdded(Entity e) {
        // Add a sensor
        Player p = e.get(Player.class);
        PhysicsBody b = e.get(PhysicsBody.class);

        PolygonShape shape = new PolygonShape();
        Fixture f = b.body.getFixtureList();
        AABB aabb = f.getAABB(0);
        Vec2 ex = aabb.getExtents();
        shape.setAsBox(ex.x/2, 2 * PhysicsSystem.WORLD_TO_BOX, new Vec2(0, ex.y), 0);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.userData = e;
        p.groundFixture = b.body.createFixture(fdef);
    }

    @Override
    public void update(double dt) {
        TouchInputData inputData = mGame.getTouchInputData();
        for (Entity e : mEntities) {
            Transformation t = e.get(Transformation.class);
            PhysicsBody b = e.get(PhysicsBody.class);
            Player p = e.get(Player.class);

            Vec2 v = b.body.getLinearVelocity();

            // Check if on ground
            boolean onGround = false;
            for (PhysicsBody.Collision c: b.collisions) {
                if (c.myFixture == p.groundFixture) {
                    onGround = true;
                    break;
                }
            }

            if (inputData.isMouseDown) {
                if (inputData.dx > 5)
                    p.moveDirection = 1;
                else if (inputData.dx < -5)
                    p.moveDirection = -1;

                if (inputData.dy < -5 && onGround)
                    b.body.applyLinearImpulse(new Vec2(0, -3.5f), b.body.getWorldCenter());
            }
            else
                p.moveDirection = 0;

            b.body.setLinearVelocity(new Vec2(p.moveDirection * 3, v.y));

            Camera camera = mGame.getRenderer().getCamera();
            float w = mGame.getRenderer().width;
            float h = mGame.getRenderer().height;
            camera.x = Math.min(Math.max(w/2, t.x), w*2-w/2) - w/2;
            camera.y = Math.min(h/2, t.y) - h/2;
        }

    }
}
