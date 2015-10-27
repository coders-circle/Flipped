package com.toggle.flipped;

import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Component;

import org.jbox2d.dynamics.Fixture;

public class Bot implements Component {
    enum Direction { LEFT, RIGHT };
    enum MotionState { IDLE, WALK, JUMP, FIGHT };

    Direction direction;
    MotionState motionState;

    // A ground sensor required to sense if bot is on solid ground
    Fixture groundFixture;

    Sprite.SpriteSheetData idle, walk, jump, fight;
}
