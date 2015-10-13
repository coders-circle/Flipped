package com.toggle.katana2d;

public class Transformation implements Component {
    public float x, y, angle;

    public Transformation(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }
}
