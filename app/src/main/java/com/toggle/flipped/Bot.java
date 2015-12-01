package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Component;
import com.toggle.katana2d.Texture;

public class Bot implements Component {
    enum Direction { LEFT, RIGHT };

    // Note that the following two states are different
    // since one can be moving and jumping at the same time.
    enum MotionState { IDLE, MOVE };
    enum ActionState { NOTHING, JUMP_START, JUMP, HANG, HANG_UP};

    Direction direction = Direction.RIGHT;
    MotionState motionState = MotionState.IDLE;
    ActionState actionState = ActionState.NOTHING;

    // Sensors required to sense if bot is colliding on ground or on sides
    Fixture groundFixture;
    Fixture leftsideFixture, rightsideFixture;

    // Contacts
    int groundContacts=0, leftSideContacts=0, rightSideContacts=0, hangingContacts=0;
    Joint hangingJoint; Fixture hanger;

    // Sprites and sprite-sheet-data for different states
    Sprite.SpriteSheetData ssdIdle, ssdWalk, ssdJump, ssdPush, ssdClimb;
    Texture sprIdle, sprWalk, sprJump, sprPush, sprClimb;
}
