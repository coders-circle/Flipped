package com.toggle.katana2d.physics;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.System;
import com.toggle.katana2d.Transformation;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

// Uses box2d to update all entities with PhysicsBody and Transformation components
public class PhysicsSystem extends System {
    public static World world = new World(new Vec2(0, -10));

    PhysicsSystem()
    {
        super(new Class[]{PhysicsBody.class, Transformation.class});
    }

    @Override
    public void update(double dt) {
        world.step((float)dt, 10, 8);

        for (Entity entity: mEntities) {
            Transformation t = entity.get(Transformation.class);
            PhysicsBody b = entity.get(PhysicsBody.class);
            t.x = b.body.getPosition().x;
            t.y = b.body.getPosition().y;
            t.angle = b.body.getAngle();
        }
    }
}
