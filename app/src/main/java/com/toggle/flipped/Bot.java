package com.toggle.flipped;

import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Component;
import com.toggle.katana2d.Texture;

import org.jbox2d.dynamics.Fixture;

public class Bot implements Component {
    enum Direction { LEFT, RIGHT };

    // Note that the following two states are different
    // since one can be moving and jumping at the same time.
    enum MotionState { IDLE, MOVE };
    enum ActionState { NOTHING, JUMP_START, JUMP, FIGHT};

    Direction direction = Direction.RIGHT;
    MotionState motionState = MotionState.IDLE;
    ActionState actionState = ActionState.NOTHING;

    // Sensors required to sense if bot is colliding on ground or on sides
    Fixture groundFixture;
    Fixture leftsideFixture, rightsideFixture;

    // Health, damagePoints

    // Sprites and sprite-sheet-data for different states
    Sprite.SpriteSheetData ssdIdle, ssdWalk, ssdJump, ssdPush;
    Texture sprIdle, sprWalk, sprJump, sprPush;
}
