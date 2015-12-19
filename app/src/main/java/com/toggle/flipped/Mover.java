package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.Component;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Transformation;

public class Mover implements Component {
    public Entity player;
    public boolean moving = false;
    public boolean reverse = true;
    public boolean cameraFocus = true;
    public boolean canReverse = false;

    public void start(Transformation current, boolean reverse) {
        canReverse = reverse;
        this.reverse = reverse && !this.reverse;

        float fx = finalX, fy = finalY, fangle = finalAngle;
        if (this.reverse) {
            fx = initialX; fy = initialY; fangle = initialAngle;
        }

        if (type == Type.LINEAR) {
            lVelocity = new Vector2(fx, fy).sub(current.x, current.y).nor();//.scl(1f);
        } else if (type == Type.ANGULAR) {
            aVelocity = Math.signum(fangle - current.angle) * 0.8f;
        }

        moving = true;
    }

    enum Type {LINEAR, ANGULAR}

    public Type type;

    public float finalX;
    public float finalY;
    public float finalAngle;
    public Vector2 lVelocity = new Vector2(0, 0);
    public float aVelocity = 0;

    public float initialX, initialY, initialAngle;
}
