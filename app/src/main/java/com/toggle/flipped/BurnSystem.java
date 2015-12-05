package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.ContactListener;
import com.toggle.katana2d.physics.PhysicsBody;

public class BurnSystem extends com.toggle.katana2d.System implements QueryCallback {

    public BurnSystem() {
        super(new Class[]{Burner.class, Transformation.class, PhysicsBody.class});
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            Burner b = entity.get(Burner.class);
            Transformation t = entity.get(Transformation.class);
            PhysicsBody pb = entity.get(PhysicsBody.class);

            if (!b.source) {
                if (b.life > 0) {
                    b.life -= dt;
                    b.isBurning = true;
                }
                else if (b.life <= 0)
                    b.isBurning = false;

                Emitter e = b.emitter.get(Emitter.class);
                e.emissionRate = Math.max(b.life * 20, 0);
            } else {
                // We are source, check if there is burnable in our area
                Vector2 pos = pb.body.getPosition();
                pb.body.getWorld().QueryAABB(this,
                        pos.x - b.sourceWidth/2, pos.y - b.sourceHeight/2,
                        pos.x + b.sourceWidth/2, pos.y + b.sourceHeight/2
                );
            }

            Transformation et = b.emitter.get(Transformation.class);
            et.x = (b.reflect?-1:1) *(float)Math.cos(Math.toRadians((b.reflect?-1:1) *t.angle)) * b.position + t.x;
            et.y = (float)Math.sin(Math.toRadians((b.reflect?-1:1) *t.angle)) * b.position + t.y;
        }
    }

    @Override
    public boolean reportFixture(Fixture other) {
        Entity otherEntity = (Entity)other.getUserData();
        if (other.isSensor() && otherEntity.has(Burner.class)) {
            Burner b = otherEntity.get(Burner.class);
            b.life = b.fullLife;
        }
        return true;
    }
}
