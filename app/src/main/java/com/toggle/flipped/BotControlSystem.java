package com.toggle.flipped;

import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Component;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

public class BotControlSystem extends com.toggle.katana2d.System {

    public BotControlSystem() {
        super(new Class[] { Player.class, Bot.class, Transformation.class, PhysicsBody.class, Sprite.class });
    }

    @Override
    public void update(double dt) {
        for (Entity e : mEntities) {
            Transformation t = e.get(Transformation.class);
            PhysicsBody b = e.get(PhysicsBody.class);
            Player p = e.get(Player.class);
            Sprite s = e.get(Sprite.class);


        }

    }
}
