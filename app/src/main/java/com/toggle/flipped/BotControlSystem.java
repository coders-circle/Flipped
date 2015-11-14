package com.toggle.flipped;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;

public class BotControlSystem extends com.toggle.katana2d.System {

    public BotControlSystem() {
        super(new Class[] { Player.class, Bot.class, Transformation.class, PhysicsBody.class, Sprite.class });
    }

    @Override
    public void onEntityAdded(Entity e) {
        // Add sensors
        Bot p = e.get(Bot.class);
        PhysicsBody b = e.get(PhysicsBody.class);
        Vec2 ex = b.body.getFixtureList().getAABB(0).getExtents();

        float twopixels =  2 * PhysicsSystem.METERS_PER_PIXEL;

        // A 2 pixel sensor at the bottom to sense the ground
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(ex.x*0.7f, twopixels, new Vec2(0, ex.y), 0);
        p.groundFixture = b.createSensor(shape);

        // Side sensors
        shape = new PolygonShape();
        shape.setAsBox(twopixels, ex.y/2, new Vec2(-ex.x, 0), 0);
        p.leftsideFixture = b.createSensor(shape);
        shape = new PolygonShape();
        shape.setAsBox(twopixels, ex.y/2, new Vec2(ex.x, 0), 0);
        p.rightsideFixture = b.createSensor(shape);
    }

    @Override
    public void update(double dt) {
        for (Entity e : mEntities) {
            Transformation t = e.get(Transformation.class);
            PhysicsBody b = e.get(PhysicsBody.class);
            Player p = e.get(Player.class);
            Sprite s = e.get(Sprite.class);
            Bot bot = e.get(Bot.class);

            Vec2 v = b.body.getLinearVelocity();

            // Check if the bot foot is touching something solid.
            // that is, if the groundFixture has collided with something.
            boolean onGround = false;
            boolean onLeftSide = false, onRightSide = false;
            for (PhysicsBody.Collision c: b.collisions) {
                if (!c.otherFixture.isSensor()) {
                    if (c.myFixture == bot.groundFixture)
                        onGround = true;
                    else if (c.myFixture == bot.leftsideFixture)
                        onLeftSide = true;
                    else if (c.myFixture == bot.rightsideFixture)
                        onRightSide = true;
                }
            }
            if (onGround)
                b.body.getFixtureList().setFriction(0.5f);
            else
                b.body.getFixtureList().setFriction(0.0f);

            // If we are on ground but we are in JUMP state, revert to NOTHING action state
            if (bot.actionState == Bot.ActionState.JUMP && onGround)
                bot.actionState = Bot.ActionState.NOTHING;

            // On jump start, check if the bot is standing on ground then apply
            // linear impulse to jump.
            else if (bot.actionState == Bot.ActionState.JUMP_START) {
                if (onGround) {
                    b.body.applyLinearImpulse(new Vec2(0, -3f * b.body.getMass()), b.body.getWorldCenter());

                    // Change to jumping state
                    bot.actionState = Bot.ActionState.JUMP;
                }
                else
                    bot.actionState = Bot.ActionState.NOTHING;
            }

            // set horizontal velocity according to current moving direction.
            float speed = 0;
            if (bot.motionState == Bot.MotionState.MOVE) {
                if (bot.direction == Bot.Direction.LEFT)
                    speed = -3;
                else
                    speed = 3;
            }
            b.body.setLinearVelocity(new Vec2(speed, v.y));

            // change sprites
            if (onGround) {
                if (speed != 0) {
                    if ((speed < 0 && onLeftSide) || (speed > 0 && onRightSide))
                        s.changeSprite(bot.sprPush, bot.ssdPush);
                    else
                        s.changeSprite(bot.sprWalk, bot.ssdWalk);
                }
                else
                    s.changeSprite(bot.sprIdle, bot.ssdIdle);
            }
            else
                s.changeSprite(bot.sprJump, bot.ssdJump);

            if (speed != 0)
                s.isReflected = speed < 0;
        }

    }
}
