package com.toggle.flipped;

import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsBody;

public class TriggerSystem extends com.toggle.katana2d.System {

    public TriggerSystem() {
        super(new Class[]{Trigger.class, PhysicsBody.class});
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            Trigger trigger = entity.get(Trigger.class);
            PhysicsBody b = entity.get(PhysicsBody.class);

            float angle = trigger.leverPosition * 30 + 60;

            //b.body.applyTorque(angle, false);
            b.body.setTransform(b.body.getPosition(), (float)Math.toRadians(angle));
        }
    }
}
