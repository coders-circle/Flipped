package com.toggle.flipped;


import com.toggle.katana2d.Entity;
import com.toggle.katana2d.GLSprite;
import com.toggle.katana2d.ParticleSystem;
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
        ParticleSystem particleSystem = new ParticleSystem(mGame);

        // Add the systems
        mSystems.add(new RenderSystem());
        mSystems.add(physicsSystem);
        mSystems.add(particleSystem);

        mSystems.add(new BotControlSystem());
        mSystems.add(new PlayerInputSystem(mGame));
        mSystems.add(flipSystem);

        // Some sprites which are just colored boxes
        mGame.getRenderer().setBackgroundColor(0, 0, 0);
        float w = mGame.getRenderer().width, h = mGame.getRenderer().height;

        int spr0 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{0.7f, 0.7f, 0.7f, 1}, 32, 32)
        );
        int spr2 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{0.2f, 0.2f, 0.2f, 1.0f}, w, 32)
        );
        int spr3 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{0.2f, 0.2f, 0.2f, 1.0f}, w * 2, 32)
        );
        int spr4 = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), null, new float[]{0.5f, 0.5f, 0.0f, 1.0f}, 32, 4)
        );

        // player sprites
        int sprRun = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), mGame.getRenderer().addTexture(R.drawable.runnin), 24, 48)
        );
        int sprPush = mGame.spriteManager.add(
                new GLSprite(mGame.getRenderer(), mGame.getRenderer().addTexture(R.drawable.pushin), 24, 48)
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
        body.add(new Transformation(w / 2, h - 32 - 16, 0));
        body.add(new Sprite(mGame.spriteManager.get(spr0)));
        body.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.DYNAMIC, body, new PhysicsBody.Properties(0.8f)));
        addEntity(body);

        Entity player = new Entity();
        player.add(new Transformation(w / 4, h - 32 - 16, 0));
        player.add(new Sprite(mGame.spriteManager.get(sprRun)));
        player.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.DYNAMIC, player, new PhysicsBody.Properties(1f, 0f, 0f, false, true)));
        player.add(new Player());
        player.add(new Bot());

        Bot playerBot = player.get(Bot.class);

        Sprite.SpriteSheetData stand = new Sprite.SpriteSheetData();
        stand.imgWidth = 139;
        stand.imgHeight = 276;
        stand.numRows = 2; stand.numCols = 5;
        stand.animationSpeed = 0;
        stand.index = 0; //5;
        playerBot.ssdIdle = stand;
        playerBot.sprIdle = mGame.spriteManager.get(sprRun);

        Sprite.SpriteSheetData walk = new Sprite.SpriteSheetData();
        walk.imgWidth = 139;
        walk.imgHeight = 276;
        walk.numRows = 2; walk.numCols = 5;
        playerBot.ssdWalk = walk;
        playerBot.sprWalk = mGame.spriteManager.get(sprRun);

        Sprite.SpriteSheetData jump = new Sprite.SpriteSheetData();
        jump.imgWidth = 139;
        jump.imgHeight = 276;
        jump.numRows = 2; jump.numCols = 5;
        jump.animationSpeed = 0;
        jump.index = 2;
        playerBot.ssdJump = jump;
        playerBot.sprJump = mGame.spriteManager.get(sprRun);

        Sprite.SpriteSheetData push = new Sprite.SpriteSheetData();
        push.imgWidth = 154;
        push.imgHeight = 276;
        push.numRows = 2; push.numCols = 5;
        push.numImages = 7;
        playerBot.ssdPush = push;
        playerBot.sprPush = mGame.spriteManager.get(sprPush);

        player.get(Sprite.class).spriteSheetData = stand;
        addEntity(player);


        // FlipSystem detects player interaction with flip-items like mirrors.
        // Set the player to check interaction with.
        flipSystem.setPlayer(player);

        /*// Create a emitter
        Entity emitter = new Entity();
        emitter.add(new Transformation(w / 2 + 32, h - 32 - 16, -90));
        emitter.add(new Emitter(mGame.getRenderer(), 300, mGame.getRenderer().mFuzzyTexture, 3, 100, new float[]{51f / 255, 102f / 255, 179f / 255, 1}, new float[]{0, 0, 0, 0}));
        addEntity(emitter);

        Emitter e = emitter.get(Emitter.class);
        e.var_startColor[3] = 0.3f;
        e.size = 20;
        e.var_size = 5;
        e.var_angle = 90;
        e.speed = 10;
        e.var_speed = 5;
        e.accel_x = 20;
        e.additiveBlend = true;*/
    }
}
