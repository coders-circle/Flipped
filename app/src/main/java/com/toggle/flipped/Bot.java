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
    enum MotionState { IDLE, MOVE }
    enum ActionState { NOTHING, JUMP_START, JUMP, HANG, HANG_UP, PICK, CARRY }
    boolean actionStart;

    Direction direction = Direction.RIGHT;
    MotionState motionState = MotionState.IDLE;
    ActionState actionState = ActionState.NOTHING;

    float idleTime = 0;

    // Sensors required to sense if bot is colliding on ground or on sides
    Fixture groundFixture;
    Fixture leftSideFixture, rightSideFixture;
    Fixture aheadSensorLeft, aheadSensorRight;

    // Carrying object
    Carriable carriable;
    float dropTime = 0;    // disable picking as soon as dropping

    float touchY;
    float touchX;

    // Contacts
    int groundContacts=0, leftSideContacts=0, rightSideContacts=0, hangingContacts=0,
        aheadLeftContacts=0, aheadRightContacts=0;
    Joint hangingJoint; Fixture hanger;
    Fixture aheadLeft, aheadRight;

    // Sprites and sprite-sheet-data for different states
    Sprite.SpriteSheetData ssdIdle, ssdWalk, ssdJump, ssdPush, ssdClimb, ssdPick, ssdCarry;
    Texture sprIdle, sprWalk, sprJump, sprPush, sprClimb, sprPick, sprCarry;
}
