package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.Component;
import com.toggle.katana2d.Transformation;

public class Mover implements Component {
    public void start(Transformation current) {
        if (type == Type.LINEAR) {
            lVelocity = new Vector2(finalX, finalY).sub(current.x, current.y).nor().scl(0.5f);
        }
        else if (type == Type.ANGULAR) {
            aVelocity = Math.signum(finalAngle - current.angle) * 0.8f;
        }
    }

    enum Type {LINEAR, ANGULAR}

    public Type type;

    public float finalX;
    public float finalY;
    public float finalAngle;
    public Vector2 lVelocity = new Vector2(0, 0);
    public float aVelocity = 0;
}
