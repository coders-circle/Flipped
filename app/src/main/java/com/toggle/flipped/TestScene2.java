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
        FlipSystem flipSystem = new FlipSystem(mGame.getRenderer().getCamera());

        // Add the systems
        mSystems.add(new RenderSystem());
        mSystems.add(physicsSystem);
        mSystems.add(new PlayerControlSystem(mGame));
        mSystems.add(flipSystem);

        // Some sprites which are just colored boxes
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
        int spr4 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{0.5f,0.5f,0.0f,1.0f}, 32, 4)
        );

        // Add some entities

        Entity ground = new Entity();
        ground.add(new Transformation(w, h - 16, 0));
        ground.add(new Sprite(mGame.spriteManager.get(spr3)));
        ground.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.STATIC, ground, new PhysicsBody.Properties(0)));
        addEntity(ground);

        Entity platform = new Entity();
        platform.add(new Transformation(w, h / 2 + 16, 0));
        platform.add(new Sprite(mGame.spriteManager.get(spr2)));
        platform.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.STATIC, platform, new PhysicsBody.Properties(0)));
        addEntity(platform);

        Entity mirror1 = new Entity();
        mirror1.add(new Transformation(w, h / 2 + 2, 0));
        mirror1.add(new Sprite(mGame.spriteManager.get(spr4)));
        mirror1.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.STATIC, mirror1, new PhysicsBody.Properties(true)));
        mirror1.add(new FlipSystem.FlipItem(FlipSystem.FlipItem.FlipItemType.MIRROR));
        addEntity(mirror1);

        mirror1.get(FlipSystem.FlipItem.class).targetAngle = 180;

        Entity mirror2 = new Entity();
        mirror2.add(new Transformation(w, h - 32 + 2, 0));
        mirror2.add(new Sprite(mGame.spriteManager.get(spr4)));
        mirror2.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.STATIC, mirror2, new PhysicsBody.Properties(true)));
        mirror2.add(new FlipSystem.FlipItem(FlipSystem.FlipItem.FlipItemType.MIRROR));
        addEntity(mirror2);

        mirror2.get(FlipSystem.FlipItem.class).targetAngle = 0;

        Entity body = new Entity();
        body.add(new Transformation(w/2, h-32-16, 0));
        body.add(new Sprite(mGame.spriteManager.get(spr0)));
        body.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.DYNAMIC, body, new PhysicsBody.Properties(0.4f)));
        addEntity(body);

        Entity player = new Entity();
        player.add(new Transformation(w/4, h-32-16, 0));
        player.add(new Sprite(mGame.spriteManager.get(spr1)));
        player.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.DYNAMIC, player, new PhysicsBody.Properties(1.0f, 0.5f, 0.0f, false, true)));
        player.add(new PlayerControlSystem.Player());
        addEntity(player);


        // FlipSystem detects player interaction with flip-items like mirrors.
        // Set the player to check interaction with.
        flipSystem.setPlayer(player);
    }
}
