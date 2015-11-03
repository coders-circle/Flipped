package com.toggle.flipped;

import com.toggle.katana2d.GLSprite;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Component;

import org.jbox2d.dynamics.Fixture;

public class Bot implements Component {
    enum Direction { LEFT, RIGHT };

    // Note that the following two states are different
    // since one can be moving and jumping at the same time.
    enum MotionState { IDLE, MOVE };
    enum ActionState { NOTHING, JUMP_START, JUMP, FIGHT};

    Direction direction;
    MotionState motionState;
    ActionState actionState;

    // Sensors required to sense if bot is colliding on ground or on sides
    Fixture groundFixture;
    Fixture leftsideFixture, rightsideFixture;

    // Health, damagePoints

    Sprite.SpriteSheetData ssdIdle, ssdWalk, ssdJump, fight, ssdPush;
    GLSprite sprIdle, sprWalk, sprJump, sprFight, sprPush;
}
