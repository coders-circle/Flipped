package com.toggle.flipped;

import com.toggle.katana2d.*;

public class BurnSystem extends com.toggle.katana2d.System {

    public BurnSystem() {
        super(new Class[]{Burner.class, Transformation.class});
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            Burner b = entity.get(Burner.class);
            Transformation t = entity.get(Transformation.class);

            Transformation et = b.emitter.get(Transformation.class);
            et.x = (b.reflect?-1:1) *(float)Math.cos(Math.toRadians((b.reflect?-1:1) *t.angle)) * b.position + t.x;
            et.y = (float)Math.sin(Math.toRadians((b.reflect?-1:1) *t.angle)) * b.position + t.y;
        }
    }
}
