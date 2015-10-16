package com.toggle.katana2d.physics;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.System;
import com.toggle.katana2d.Transformation;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

// Uses box2d to update all entities with PhysicsBody and Transformation components
public class PhysicsSystem extends System {
    public static final float BOX_TO_WORLD = 30f;
    public static final float WORLD_TO_BOX = 1 / BOX_TO_WORLD;

    private World world = new World(new Vec2(0, 10));

    public PhysicsSystem()
    {
        super(new Class[]{PhysicsBody.class, Transformation.class});
    }

    public World getWorld() { return world; }

    @Override
    public void update(double dt) {
        world.step((float)dt, 10, 8);

        for (Entity entity: mEntities) {
            Transformation t = entity.get(Transformation.class);
            PhysicsBody b = entity.get(PhysicsBody.class);
            t.x = b.body.getPosition().x * BOX_TO_WORLD;
            t.y = b.body.getPosition().y * BOX_TO_WORLD;
            t.angle = (float)Math.toDegrees(b.body.getAngle());
        }
    }
}
