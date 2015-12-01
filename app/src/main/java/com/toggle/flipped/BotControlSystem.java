package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.ContactListener;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

public class BotControlSystem extends com.toggle.katana2d.System implements ContactListener {

    public BotControlSystem() {
        super(new Class[] { Bot.class, Transformation.class, PhysicsBody.class, Sprite.class });
    }

    @Override
    public void onEntityAdded(Entity e) {
        // Add sensors
        Bot p = e.get(Bot.class);
        PhysicsBody b = e.get(PhysicsBody.class);
        //Vector2 ex = b.body.getFixtureList().get(0).getAABB(0).getExtents();
        //b.body.getFixtureList().get(0).setFriction(0);

        Sprite s = e.get(Sprite.class);
        Vector2 ex = new Vector2(s.texture.width/2 * PhysicsSystem.METERS_PER_PIXEL - 0.01f, s.texture.height/2 * PhysicsSystem.METERS_PER_PIXEL - 0.01f);

        float sensorSize =  2 * PhysicsSystem.METERS_PER_PIXEL;

        PolygonShape shape = new PolygonShape();

        // A sensor at the bottom to sense the ground
        shape.setAsBox(ex.x*0.83f, sensorSize, new Vector2(0, ex.y), 0);
        p.groundFixture = b.createSensor(shape);

        // Side sensors
        shape = new PolygonShape();
        shape.setAsBox(sensorSize, ex.y/2, new Vector2(-ex.x, 0), 0);
        p.leftsideFixture = b.createSensor(shape);
        shape = new PolygonShape();
        shape.setAsBox(sensorSize, ex.y/2, new Vector2(ex.x, 0), 0);
        p.rightsideFixture = b.createSensor(shape);

        b.contactListener = this;
    }

    @Override
    public void update(float dt) {
        for (Entity e : mEntities) {
            //Transformation t = e.get(Transformation.class);
            final PhysicsBody b = e.get(PhysicsBody.class);
            final Sprite s = e.get(Sprite.class);
            final Bot bot = e.get(Bot.class);

            float directionFactor = bot.direction== Bot.Direction.RIGHT?1:-1;

            boolean onGround = bot.groundContacts>0;
            boolean onLeftSide = bot.leftSideContacts>0, onRightSide = bot.rightSideContacts>0;

            boolean toHang = bot.hangingContacts > 0
                    && !onGround && ((onRightSide && bot.direction == Bot.Direction.RIGHT)
                    || (onLeftSide && bot.direction == Bot.Direction.LEFT));
            if (onGround)
                b.body.getFixtureList().get(0).setFriction(0.2f);
            else
                b.body.getFixtureList().get(0).setFriction(0.0f);

            if (toHang) {
                if (bot.actionState != Bot.ActionState.HANG && bot.actionState != Bot.ActionState.HANG_UP)
                    bot.actionState = Bot.ActionState.HANG;
            }
            else if (bot.actionState == Bot.ActionState.HANG_UP || bot.actionState == Bot.ActionState.HANG) {
                b.body.setTransform(b.body.getPosition().add(
                        32*directionFactor*PhysicsSystem.METERS_PER_PIXEL, 24* PhysicsSystem.METERS_PER_PIXEL), 0);
                bot.actionState = Bot.ActionState.NOTHING;
                bot.hangingContacts = bot.leftSideContacts = bot.rightSideContacts = 0;
            }

            if (bot.actionState == Bot.ActionState.HANG) {
                b.body.setType(BodyDef.BodyType.KinematicBody);
                b.body.setLinearVelocity(new Vector2(0, 0));
                b.body.setTransform(bot.hanger.getBody().getPosition().sub(
                        10 * directionFactor * PhysicsSystem.METERS_PER_PIXEL,
                        3 * PhysicsSystem.METERS_PER_PIXEL), 0);

                bot.ssdClimb.index = 0;
                bot.ssdClimb.animationSpeed = 0;
                s.changeSprite(bot.sprClimb, bot.ssdClimb);
            }
            else if (bot.actionState == Bot.ActionState.HANG_UP) {
                bot.ssdClimb.animationSpeed = 12;
                bot.ssdClimb.listener = new Sprite.AnimationListener() {
                    @Override
                    public void onComplete() {
                        b.body.setTransform(bot.hanger.getBody().getPosition().sub(
                                0, (16+24) * PhysicsSystem.METERS_PER_PIXEL), 0);
                        bot.actionState = Bot.ActionState.NOTHING;
                        bot.hangingContacts = bot.leftSideContacts = bot.rightSideContacts = 0;
                    }
                };
                s.changeSprite(bot.sprClimb, bot.ssdClimb);
            }
            else {
                b.body.setType(BodyDef.BodyType.DynamicBody);
            }

            // If we are on ground but we are in JUMP state, revert to NOTHING action state
            if (bot.actionState == Bot.ActionState.JUMP && onGround)
                bot.actionState = Bot.ActionState.NOTHING;

            // On jump start, check if the bot is standing on ground then apply
            // linear impulse to jump.
            else if (bot.actionState == Bot.ActionState.JUMP_START) {
                if (onGround) {
                    b.body.applyLinearImpulse(new Vector2(0, -5f * b.body.getMass()), b.body.getWorldCenter(), false);

                    // Change to jumping state
                    bot.actionState = Bot.ActionState.JUMP;
                }
                else
                    bot.actionState = Bot.ActionState.NOTHING;
            }

            // set horizontal velocity according to current moving direction.
            Vector2 vel = b.body.getLinearVelocity();
            float speed = 0;
            if (bot.motionState == Bot.MotionState.MOVE)
                speed = 3f * directionFactor;

            if (b.body.getType() == BodyDef.BodyType.DynamicBody) {
                float force = b.body.getMass() * (speed - vel.x) / dt;    // f = mv/t ; v = required change in velocity
                b.body.applyForce(new Vector2(force, 0), b.body.getWorldCenter(), false);
            }
            else
                b.body.setLinearVelocity(speed, vel.y);

            // limit upward vertical velocity
            /*float maxVelY = -12;
            if (vel.y < maxVelY) {
                float force1 = b.body.getMass() * (maxVelY - vel.y) / dt;
                b.body.applyForce(new Vector2(0, force1), b.body.getWorldCenter(), false);
            }*/

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
            else if (bot.actionState != Bot.ActionState.HANG
                    && bot.actionState != Bot.ActionState.HANG_UP)
                s.changeSprite(bot.sprJump, bot.ssdJump);

            if (speed != 0)
                s.scaleX = speed < 0? -1 : 1;
        }

    }

    @Override
    public void beginContact(Contact contact, Fixture me, Fixture other) {
        Bot bot = ((Entity)me.getUserData()).get(Bot.class);

        if (other.isSensor()) {
            if ((me == bot.leftsideFixture || me == bot.rightsideFixture)
                    && ((Entity)other.getUserData()).has(Hanger.class)) {
                bot.hanger = other;
                bot.hangingContacts++;
            }
            return;
        }

        if (me == bot.groundFixture) {
            if (!((Entity)other.getUserData()).has(PlatformSystem.OneWayPlatform.class)
                    || me.getBody().getLinearVelocity().y >= 0)
                bot.groundContacts++;
        }
        else if (me == bot.leftsideFixture)
            bot.leftSideContacts++;
        else if (me == bot.rightsideFixture)
            bot.rightSideContacts++;
    }

    @Override
    public void endContact(Contact contact, Fixture me, Fixture other) {
        Bot bot = ((Entity)me.getUserData()).get(Bot.class);

        if (other.isSensor()) {
            if ((me == bot.leftsideFixture || me == bot.rightsideFixture)
                    && ((Entity) other.getUserData()).has(Hanger.class)
                    && bot.hangingContacts > 0)
                bot.hangingContacts--;
        }

        if (me == bot.groundFixture && bot.groundContacts > 0)
                bot.groundContacts--;
        else if (me == bot.leftsideFixture && bot.leftSideContacts > 0)
            bot.leftSideContacts--;
        else if (me == bot.rightsideFixture && bot.rightSideContacts > 0)
            bot.rightSideContacts--;
    }

    @Override
    public void preSolve(Contact contact, Fixture me, Fixture other) {

    }

    @Override
    public void postSolve(Contact contact, Fixture me, Fixture other) {

    }
}
