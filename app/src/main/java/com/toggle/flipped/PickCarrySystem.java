package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

public class PickCarrySystem extends com.toggle.katana2d.System {
    public PickCarrySystem() {
        super(new Class[]{Carriable.class, PhysicsBody.class});
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            Carriable c = entity.get(Carriable.class);
            PhysicsBody b = entity.get(PhysicsBody.class);

            // On normal state, this is a normal dynamic body
            if (c.state == Carriable.State.NOTHING) {
                b.body.setType(BodyDef.BodyType.DynamicBody);
                b.body.getFixtureList().get(0).setSensor(false);
                //b.body.setActive(true);
                continue;
            }

            // otherwise we set it to kinematic body to set position manually
            b.body.setType(BodyDef.BodyType.KinematicBody);
            b.body.getFixtureList().get(0).setSensor(true);
            //b.body.setActive(false);

            Vector2 pos = new Vector2(0, 0);
            float angle = 0;
            if (c.state == Carriable.State.PICKED) {
                int index = c.carrier.get(Bot.class).ssdPick.index;
                pos = new Vector2(c.positions.get(index));
                angle = c.angles.get(index);
            } else if (c.state == Carriable.State.CARRIED) {
                pos = new Vector2(c.position);
                angle = c.angle;
            }

            if (c.carrier.get(Bot.class).direction == Bot.Direction.LEFT) {
                pos.x *= -1;
                pos.x -= 2;
                angle *= -1;
                if (entity.has(Burner.class)) {
                    entity.get(Burner.class).reflect = true;
                }
            } else  if (entity.has(Burner.class)) {
                pos.x += 2;
                entity.get(Burner.class).reflect = false;
            }

            b.body.setTransform(
                    pos.scl(PhysicsSystem.METERS_PER_PIXEL).add(c.carrier.get(PhysicsBody.class).body.getPosition()),
                    (float)Math.toRadians(angle));
        }
    }
}
