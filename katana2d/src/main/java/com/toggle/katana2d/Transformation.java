package com.toggle.katana2d;

// Transformation component to store position and angle of entity
public class Transformation implements Component {
    public float x, y, angle;
    //public float vel_x = 0, vel_y = 0, vel_angle = 0;
    public float lastX, lastY, lastAngle;

    public void saveState() {
        lastX = x;
        lastY = y;
        lastAngle = angle;
    }

    public Transformation(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        saveState();
    }
}
