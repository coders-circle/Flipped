package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsBody;

public class AutoMoverSystem extends com.toggle.katana2d.System {
    public AutoMoverSystem() {
        super(new Class[]{Mover.class, PhysicsBody.class, Transformation.class});
    }

    @Override
    public void update(float dt) {
        for (Entity e: mEntities) {
            PhysicsBody b = e.get(PhysicsBody.class);
            Transformation t = e.get(Transformation.class);
            Mover m = e.get(Mover.class);

            if (m.type == Mover.Type.LINEAR) {
                if (Math.abs(t.x - m.finalX) < 8 && Math.abs(t.y - m.finalY) < 8)
                    b.body.setLinearVelocity(new Vector2(0, 0));
                else
                    b.body.setLinearVelocity(m.lVelocity);
            }
            else if (m.type == Mover.Type.ANGULAR) {
                if (Math.abs(t.angle - m.finalAngle) < 3)
                    b.body.setAngularVelocity(0);
                else
                    b.body.setAngularVelocity(m.aVelocity);
            }

        }
    }
}
