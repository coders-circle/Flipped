package com.toggle.flipped;


import com.badlogic.gdx.physics.box2d.BodyDef;
import com.toggle.katana2d.*;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

public class TestScene extends Scene {

    @Override
    public void onInit() {
        PhysicsSystem physicsSystem = new PhysicsSystem();
        // Add the systems
        mSystems.add(new RenderSystem(mGame.getRenderer()));
        mSystems.add(physicsSystem);

        mGame.getRenderer().setBackgroundColor(1, 1, 1);
        float w = mGame.getRenderer().width, h = mGame.getRenderer().height;

        int spr0 = mGame.textureManager.add(
                mGame.getRenderer().addTexture(new float[]{0,0,0,1}, 32, 32)
        );
        int spr1 = mGame.textureManager.add(
                mGame.getRenderer().addTexture(new float[]{0.2f,0.2f,0.2f,1.0f}, w, 32)
        );

        Entity ground = new Entity();
        ground.add(new Transformation(w / 2, h - 16, 0));
        ground.add(new Sprite(mGame.textureManager.get(spr1), 0));
        ground.add(new PhysicsBody(physicsSystem.getWorld(), BodyDef.BodyType.StaticBody, ground, new PhysicsBody.Properties(0)));
        addEntity(ground);

        for (int i=0; i<10; ++i) {
            Entity body = new Entity();
            float x = w/2 - 32 + (i/5) * 32 - 8*i;
            float y = h/2 - 32*2 + (i%5) * 32;
            body.add(new Transformation(x, y, 48));
            body.add(new Sprite(mGame.textureManager.get(spr0), 0));
            body.add(new PhysicsBody(physicsSystem.getWorld(), BodyDef.BodyType.StaticBody, body, new PhysicsBody.Properties(1.0f)));
            addEntity(body);
        }
    }
}
