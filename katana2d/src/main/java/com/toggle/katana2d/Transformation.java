package com.toggle.katana2d;

import com.badlogic.gdx.math.Vector2;

// Transformation component to store position and angle of entity
public class Transformation implements Component {
    public float x, y, angle;

    public Transformation(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public Vector2 getPos() {
        return new Vector2(x, y);
    }
}
