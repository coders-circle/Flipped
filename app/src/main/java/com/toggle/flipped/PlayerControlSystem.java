package com.toggle.flipped;

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

        // A 2 pixel sensor at the bottom to sense the ground
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

            // Check if the player's foot is touching something solid.
            // that is, if the groundFixture has collided with something.
            boolean onGround = false;
            for (PhysicsBody.Collision c: b.collisions) {
                if (c.myFixture == p.groundFixture) {
                    onGround = true;
                    break;
                }
            }

            if (inputData.isTouchDown) {
                // when user is touching on the screen,
                // set move-direction to horizontal sliding direction ( direction of dx )
                if (inputData.dx > 5)
                    p.moveDirection = 1;
                else if (inputData.dx < -5)
                    p.moveDirection = -1;

                // if vertical sliding direction is up, and sliding magnitude is more than 8
                // and our feet are touching solid object (onGround)
                // then apply linear impulse to make tha player jump.
                if (inputData.dy < -8 && onGround)
                    b.body.applyLinearImpulse(new Vec2(0, -3.5f), b.body.getWorldCenter());
            }
            else
                p.moveDirection = 0;

            // set horizontal velocity according to current moving direction.
            b.body.setLinearVelocity(new Vec2(p.moveDirection * 3, v.y));

            // Scroll the camera so that player is at the center of screen.
            // Make sure the camera don't go off the edges of the world,
            // assuming the world is twice the width of camera view (w*2).
            // This assumption is temporary.
            Camera camera = mGame.getRenderer().getCamera();
            float w = mGame.getRenderer().width;
            float h = mGame.getRenderer().height;
            camera.x = Math.min(Math.max(w/2, t.x), w*2-w/2) - w/2;
            camera.y = Math.min(h/2, t.y) - h/2;
        }

    }
}
