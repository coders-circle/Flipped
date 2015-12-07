package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.ContactListener;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

public class BotControlSystem extends com.toggle.katana2d.System implements ContactListener {

    public BotControlSystem() {
        super(new Class[] { Bot.class, Transformation.class, PhysicsBody.class, Sprite.class, Sound.class });
    }

    @Override
    public void onEntityAdded(Entity e) {
        // Add sensors
        Bot p = e.get(Bot.class);
        PhysicsBody b = e.get(PhysicsBody.class);

        Sprite s = e.get(Sprite.class);
        Vector2 ex = new Vector2(s.texture.width/2 * PhysicsSystem.METERS_PER_PIXEL - 0.01f, s.texture.height/2 * PhysicsSystem.METERS_PER_PIXEL - 0.01f);

        float sensorSize =  2 * PhysicsSystem.METERS_PER_PIXEL;

        PolygonShape shape = new PolygonShape();

        // A sensor at the bottom to sense the ground
        shape.setAsBox(ex.x*0.83f, sensorSize*3, new Vector2(0, ex.y+sensorSize), 0);
        p.groundFixture = b.createSensor(shape);

        // Side sensors
        shape = new PolygonShape();
        shape.setAsBox(sensorSize, sensorSize*4, new Vector2(-ex.x, 0), 0);
        p.leftSideFixture = b.createSensor(shape);
        shape = new PolygonShape();
        shape.setAsBox(sensorSize, sensorSize*4, new Vector2(ex.x, 0), 0);
        p.rightSideFixture = b.createSensor(shape);

        float boxSize = 16 * PhysicsSystem.METERS_PER_PIXEL;
        shape = new PolygonShape();
        shape.setAsBox(boxSize, boxSize, new Vector2(ex.x+boxSize, ex.y-boxSize), 0);
        p.aheadSensorRight = b.createSensor(shape);
        shape = new PolygonShape();
        shape.setAsBox(boxSize, boxSize, new Vector2(-ex.x-boxSize, ex.y-boxSize), 0);
        p.aheadSensorLeft = b.createSensor(shape);

        b.contactListener = this;
    }

    @Override
    public void update(float dt) {
        for (Entity e : mEntities) {
            //Transformation t = e.get(Transformation.class);
            final PhysicsBody b = e.get(PhysicsBody.class);
            final Sprite s = e.get(Sprite.class);
            final Bot bot = e.get(Bot.class);
            final Sound sound = e.get(Sound.class);

            if (bot.actionState == Bot.ActionState.FADE_OUT) {
                s.mixColor[3] -= dt;
                if (s.mixColor[3] <= 0) {
                    s.mixColor[3] = 0;
                    bot.actionState = Bot.ActionState.FADE_COMPLETE;
                }
            }
            else if (bot.actionState == Bot.ActionState.FADE_IN) {
                s.mixColor[3] += dt;
                if (s.mixColor[3] >= 1) {
                    s.mixColor[3] = 1;
                    bot.actionState = Bot.ActionState.FADE_COMPLETE;
                }
            }

            boolean resetIdle = true;
            if (bot.actionState == Bot.ActionState.NOTHING && bot.motionState == Bot.MotionState.IDLE) {
                bot.idleTime += dt;
                if (bot.idleTime > 15) {
                    bot.idleTime = 0;
                    bot.ssdIdle.animationSpeed = 0;
                }
                else if (bot.idleTime > 5) {
                    bot.ssdIdle.animationSpeed = 12;
                    resetIdle = false;
                }
            } else {
                bot.idleTime = 0;
                bot.ssdIdle.animationSpeed = 0;
            }

            if (resetIdle) {
                bot.ssdIdle.animationSpeed = 0;
                bot.ssdIdle.index = 0;
            }

            if (bot.dropTime > 0)
                bot.dropTime -= dt;

            float directionFactor = bot.direction == Bot.Direction.RIGHT?1:-1;
            s.scaleX = directionFactor < 0? -1 : 1;

            boolean onGround = bot.groundContacts>0;
            boolean onLeftSide = bot.leftSideContacts>0 && bot.direction == Bot.Direction.LEFT, onRightSide = bot.rightSideContacts>0 && bot.direction == Bot.Direction.RIGHT;

            // Get the fixture that is ahead of us
            Fixture ahead = null;
            if (bot.direction == Bot.Direction.LEFT && bot.aheadLeftContacts > 0)
                ahead = bot.aheadLeft;
            else if (bot.direction == Bot.Direction.RIGHT && bot.aheadRightContacts > 0)
                ahead = bot.aheadRight;

            // set friction on when we are on ground
            b.body.getFixtureList().get(0).setFriction(0.0f);


            Entity sideEntity = null;
            if (bot.direction == Bot.Direction.LEFT && onLeftSide)
                sideEntity = (Entity)bot.leftFixtureObject.getUserData();
            else if (bot.direction == Bot.Direction.RIGHT && onRightSide)
                sideEntity = (Entity)bot.rightFixtureObject.getUserData();

            /*
            First handle actions
             */

            if (bot.actionStart) {
                // check what action we can perform
                if (bot.actionState == Bot.ActionState.CARRY) {
                    // drop
                    bot.actionState = Bot.ActionState.NOTHING;
                    if (bot.carriable != null) {
                        bot.carriable.state = Carriable.State.NOTHING;
                        bot.carriable.carrier = null;
                        bot.carriable = null;
                        bot.dropTime = 1;   // disable picking for 1 second
                    }
                }
                else if (ahead != null) {
                    Entity other = (Entity)ahead.getUserData();
                    if (other != null && other.has(Carriable.class) && bot.dropTime <= 0) {
                        // pickup
                        bot.actionState = Bot.ActionState.PICK;
                        bot.carriable = other.get(Carriable.class);
                        bot.carriable.state = Carriable.State.PICKED;
                        bot.carriable.carrier = e;
                        bot.leftFixtureObject = null;
                        bot.rightFixtureObject = null;
                        bot.aheadLeftContacts = 0; bot.aheadRightContacts = 0;
                    }
                }
                bot.actionStart = false;
            }

            if (bot.actionState == Bot.ActionState.PICK) {
                if (bot.ssdPick.listener == null)
                    bot.ssdPick.listener = new Sprite.AnimationListener() {
                        @Override
                        public void onComplete() {
                            bot.actionState = Bot.ActionState.CARRY;
                            bot.ssdPick.listener = null;
                        }
                    };
                s.changeSprite(bot.sprPick, bot.ssdPick);
                return;
            }
            else if (bot.actionState == Bot.ActionState.CARRY) {
                if (bot.carriable != null) {
                    bot.carriable.state = Carriable.State.CARRIED;
                }
            }
            else if (bot.actionState == Bot.ActionState.LEVER_PUSH) {
                s.changeSprite(bot.sprLever, bot.ssdLever);
                bot.lever.leverPosition = bot.ssdLever.index / 15f;
                bot.ssdLever.listener = new Sprite.AnimationListener() {
                    @Override
                    public void onComplete() {
                        bot.lever.leverPosition = 1;
                        s.changeSprite(bot.sprIdle, bot.ssdIdle);
                        bot.lever.trigger();
                        bot.actionState = Bot.ActionState.NOTHING;
                    }
                };
            }


            /*
            Next handle hang and climb
             */

            // we can hang when we touch a hanger and our direction is same as that of contact
            // and we are not on ground
            boolean toHang = bot.hangingContacts > 0
                    && !onGround && (onRightSide || onLeftSide);

            if (toHang) {
                // if we are to hang but not already in a hanging state, set the state
                if (bot.actionState != Bot.ActionState.HANG && bot.actionState != Bot.ActionState.HANG_UP) {
                    bot.actionState = Bot.ActionState.HANG;
                    bot.motionState = Bot.MotionState.IDLE;
                }
            }
            else if (bot.actionState == Bot.ActionState.HANG_UP || bot.actionState == Bot.ActionState.HANG) {
                // if we are not to hang but we are in hanging state, reset the state
                // note that this happens when we cancel a hang by trying to move opposite the direction of hanging
                bot.actionState = Bot.ActionState.NOTHING;
                bot.hangingContacts = bot.leftSideContacts = bot.rightSideContacts = 0;
            }

            if (bot.actionState == Bot.ActionState.HANG) {
                // while in hanging state, we set the body type to kinematic to disable dynamic physics
                // and set the velocity to 0
                b.body.setType(BodyDef.BodyType.KinematicBody);
                b.body.setLinearVelocity(new Vector2(0, 0));

                if (bot.direction == Bot.Direction.RIGHT)
                    b.body.setTransform(bot.hanger.getBody().getPosition().add(bot.climbPositions.get(0)), 0);
                else if (bot.direction == Bot.Direction.LEFT)
                    b.body.setTransform(bot.hanger.getBody().getPosition().add(
                            -bot.climbPositions.get(0).x, bot.climbPositions.get(0).y
                    ), 0);

                // climbing animation should be disabled
                bot.ssdClimb.index = 0;
                bot.ssdClimb.animationSpeed = 0;
                s.changeSprite(bot.sprClimb, bot.ssdClimb);
            }
            else if (bot.actionState == Bot.ActionState.HANG_UP) {
                // while moving up during hanging, climbing animation should be enabled
                bot.ssdClimb.animationSpeed = 12;

                if (bot.direction == Bot.Direction.RIGHT)
                    b.body.setTransform(bot.hanger.getBody().getPosition().add(bot.climbPositions.get(bot.ssdClimb.index)), 0);
                else if (bot.direction == Bot.Direction.LEFT)
                    b.body.setTransform(bot.hanger.getBody().getPosition().add(
                            -bot.climbPositions.get(bot.ssdClimb.index).x, bot.climbPositions.get(bot.ssdClimb.index).y
                    ), 0);

                // on completion on animation, reset the position and state
                if (bot.ssdClimb.listener == null)
                bot.ssdClimb.listener = new Sprite.AnimationListener() {
                    @Override
                    public void onComplete() {
                        bot.actionState = Bot.ActionState.NOTHING;
                        bot.hangingContacts = bot.leftSideContacts = bot.rightSideContacts = 0;
                        bot.ssdClimb.listener = null;
                    }
                };
                s.changeSprite(bot.sprClimb, bot.ssdClimb);
            }
            else {
                // make sure the body type is dynamic when are not in a hanging state
                b.body.setType(BodyDef.BodyType.DynamicBody);
            }

            /*
            Next handle jumping
             */

            // If we are on ground but we are in JUMP state, revert to NOTHING action state
            if (bot.actionState == Bot.ActionState.JUMP && onGround)
                bot.actionState = Bot.ActionState.NOTHING;

            // On jump start, check if the bot is standing on ground then apply
            // linear impulse to jump.
            else if (bot.actionState == Bot.ActionState.JUMP_START) {
                if (onGround) {
                    //TODO: Change state to jump_take_off
                    sound.state = Sound.JUMP_START;

                    float force = Math.min(6f, bot.touchY);
                    b.body.applyLinearImpulse(new Vector2(0, -force * b.body.getMass()), b.body.getWorldCenter(), false);

                    // Change to jumping state
                    bot.actionState = Bot.ActionState.JUMP;
                }
                else
                    bot.actionState = Bot.ActionState.NOTHING;
            }

            /*
            Finally handle horizontal movement
             */

            // set horizontal velocity according to current moving direction.
            Vector2 vel = b.body.getLinearVelocity();
            float speed = 0;
            float moveSpeed = Math.min(bot.touchX, 4f);
            if (bot.motionState == Bot.MotionState.MOVE)
                speed = moveSpeed * directionFactor;

            if (b.body.getType() == BodyDef.BodyType.DynamicBody) {
                float force = b.body.getMass() * (speed - vel.x) / dt;    // f = mv/t ; v = required change in velocity
                b.body.applyForce(new Vector2(force, 0), b.body.getWorldCenter(), false);
            }
            else
                b.body.setLinearVelocity(speed, vel.y);

            /*
            If moving forward with a lever in front, we activate the lever
             */

            if ((speed < 0 && onLeftSide) || (speed > 0 && onRightSide)) {
                if (sideEntity != null && sideEntity.has(Trigger.class)) {
                    bot.lever = sideEntity.get(Trigger.class);
                    if (bot.lever.type == Trigger.Type.LEVER && !bot.lever.getStatus())
                        bot.actionState = Bot.ActionState.LEVER_PUSH;
                    else
                        bot.lever = null;
                }
            }

            /*
            Now change sprites accordingly
             */

            if (onGround) {
                if (speed != 0) {
                    if (bot.actionState == Bot.ActionState.CARRY) {
                        bot.ssdCarry.animationSpeed = 3*moveSpeed;
                        s.changeSprite(bot.sprCarry, bot.ssdCarry);
                    }
                    else if (onLeftSide || onRightSide) {
                        if (bot.actionState != Bot.ActionState.LEVER_PUSH)
                            s.changeSprite(bot.sprPush, bot.ssdPush);
                    }
                    else
                        s.changeSprite(bot.sprWalk, bot.ssdWalk);
                }
                else if (bot.actionState == Bot.ActionState.CARRY) {
                    bot.ssdCarry.index = 0;
                    bot.ssdCarry.animationSpeed = 0;
                    s.changeSprite(bot.sprCarry, bot.ssdCarry);
                }
                else
                    s.changeSprite(bot.sprIdle, bot.ssdIdle);
            }
            else if (bot.actionState != Bot.ActionState.HANG
                    && bot.actionState != Bot.ActionState.HANG_UP)
                s.changeSprite(bot.sprJump, bot.ssdJump);

            bot.ssdWalk.animationSpeed = 3 * moveSpeed;
        }

    }

    @Override
    public void beginContact(Contact contact, Fixture me, Fixture other) {
        Bot bot = ((Entity)me.getUserData()).get(Bot.class);

        if (other.isSensor()) {
            if ((me == bot.leftSideFixture || me == bot.rightSideFixture)
                    && ((Entity)other.getUserData()).has(Hanger.class)) {
                bot.hanger = other;
                bot.hangingContacts++;
            }
            return;
        }

        if (!((Entity)other.getUserData()).has(Carriable.class)) {
            if (me == bot.groundFixture) {
            /*if (!((Entity)other.getUserData()).has(PlatformSystem.OneWayPlatform.class)
                    || me.getBody().getLinearVelocity().y >= 0)*/
                bot.groundContacts++;
            } else if (me == bot.leftSideFixture) {
                bot.leftSideContacts++;
                bot.leftFixtureObject = other;
            } else if (me == bot.rightSideFixture) {
                bot.rightSideContacts++;
                bot.rightFixtureObject = other;
            }
        }
        else if (me == bot.aheadSensorLeft) {
            bot.aheadLeftContacts++;
            bot.aheadLeft = other;
        }
        else if (me == bot.aheadSensorRight) {
            bot.aheadRightContacts++;
            bot.aheadRight = other;
        }
    }

    @Override
    public void endContact(Contact contact, Fixture me, Fixture other) {
        Bot bot = ((Entity)me.getUserData()).get(Bot.class);

        if (other.isSensor()) {
            if ((me == bot.leftSideFixture || me == bot.rightSideFixture)
                    && ((Entity) other.getUserData()).has(Hanger.class)
                    && bot.hangingContacts > 0)
                bot.hangingContacts--;
            return;
        }

        if (!((Entity)other.getUserData()).has(Carriable.class)) {
            if (me == bot.groundFixture && bot.groundContacts > 0)
                bot.groundContacts--;
            else if (me == bot.leftSideFixture && bot.leftSideContacts > 0)
                bot.leftSideContacts--;
            else if (me == bot.rightSideFixture && bot.rightSideContacts > 0)
                bot.rightSideContacts--;
        }
        else if (me == bot.aheadSensorLeft)
            bot.aheadLeftContacts--;
        else if (me == bot.aheadSensorRight)
            bot.aheadRightContacts--;
    }

    @Override
    public void preSolve(Contact contact, Fixture me, Fixture other) {

    }

    @Override
    public void postSolve(Contact contact, Fixture me, Fixture other) {

    }
}
