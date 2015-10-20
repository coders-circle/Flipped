package com.toggle.flipped;


import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

import org.jbox2d.dynamics.BodyType;

public class TestScene extends Scene {

    @Override
    public void onInit() {
        PhysicsSystem physicsSystem = new PhysicsSystem();
        // Add the systems
        mSystems.add(new RenderSystem());
        mSystems.add(physicsSystem);

        mGame.getRenderer().setBackgroundColor(1, 1, 1);
        float w = mGame.getRenderer().width, h = mGame.getRenderer().height;

        int spr0 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{0,0,0,1}, 32, 32)
        );
        int spr1 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{0.2f,0.2f,0.2f,1.0f}, w, 32)
        );

        Entity ground = new Entity();
        ground.add(new Transformation(w / 2, h - 16, 0));
        ground.add(new Sprite(mGame.spriteManager.get(spr1)));
        ground.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.STATIC, ground, new PhysicsBody.Properties(0)));
        addEntity(ground);

        for (int i=0; i<10; ++i) {
            Entity body = new Entity();
            float x = w/2 - 32 + (i/5) * 32 - 8*i;
            float y = h/2 - 32*2 + (i%5) * 32;
            body.add(new Transformation(x, y, 48));
            body.add(new Sprite(mGame.spriteManager.get(spr0)));
            body.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.DYNAMIC, body, new PhysicsBody.Properties(1.0f)));
            addEntity(body);
        }
    }
}
