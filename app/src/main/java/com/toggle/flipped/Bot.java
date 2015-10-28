package com.toggle.flipped;

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

    // A ground sensor required to sense if bot is on solid ground
    Fixture groundFixture;
    // Other sensors to sense e.g. enemies during fighting

    // Health, damagePoints

    Sprite.SpriteSheetData idle, walk, jump, fight;
}
