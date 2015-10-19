package com.toggle.flipped;


import com.toggle.katana2d.Entity;
import com.toggle.katana2d.GLSprite;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

import org.jbox2d.dynamics.BodyType;

public class TestScene2 extends Scene {

    @Override
    public void onInit() {
        PhysicsSystem physicsSystem = new PhysicsSystem();

        // Add the systems
        mSystems.add(new RenderSystem());
        mSystems.add(physicsSystem);
        mSystems.add(new PlayerControlSystem(mGame));

        mGame.getRenderer().setBackgroundColor(1, 1, 1);
        float w = mGame.getRenderer().width, h = mGame.getRenderer().height;

        int spr0 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{0,0,0,1}, 32, 32)
        );
        int spr1 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{1,0,0,1}, 32, 32)
        );
        int spr2 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{0.2f,0.2f,0.2f,1.0f}, w, 32)
        );
        int spr3 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{0.2f,0.2f,0.2f,1.0f}, w * 2, 32)
        );

        Entity ground = new Entity();
        ground.add(new Transformation(w, h - 16, 0));
        ground.add(new Sprite(mGame.spriteManager.get(spr3)));
        ground.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.STATIC, ground, 0, 0.2f, 0, false, false));
        addEntity(ground);

        Entity platform = new Entity();
        platform.add(new Transformation(w, h/2+16, 0));
        platform.add(new Sprite(mGame.spriteManager.get(spr2)));
        platform.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.STATIC, platform, 0, 0.2f, 0, false, false));
        addEntity(platform);




        Entity body = new Entity();
        body.add(new Transformation(w/2, h-32-16, 0));
        body.add(new Sprite(mGame.spriteManager.get(spr0)));
        body.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.DYNAMIC, body, 0.4f, 0.2f, 0.01f, false, false));
        addEntity(body);

        Entity player = new Entity();
        player.add(new Transformation(w/4, h-32-16, 0));
        player.add(new Sprite(mGame.spriteManager.get(spr1)));
        player.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.DYNAMIC, player, 1.0f, 0.5f, 0, false, true));
        player.add(new PlayerControlSystem.Player());
        addEntity(player);
    }
}
