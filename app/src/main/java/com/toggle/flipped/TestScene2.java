package com.toggle.flipped;

import com.toggle.katana2d.Emitter;
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
        mSystems.add(new WindSystem());
        mSystems.add(new RopeSystem(physicsSystem.getWorld()));

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
//
//        // player sprites
//        int sprRun = mGame.spriteManager.add(
//                new GLSprite(mGame.getRenderer(), mGame.getRenderer().addTexture(R.drawable.runnin), 24, 48)
//        );
//        int sprPush = mGame.spriteManager.add(
//                new GLSprite(mGame.getRenderer(), mGame.getRenderer().addTexture(R.drawable.pushin), 24, 48)
//        );

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
        body.add(new Transformation(w / 2-64, h - 32 - 16, 0));
        body.add(new Sprite(mGame.spriteManager.get(spr0)));
        body.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.DYNAMIC, body, new PhysicsBody.Properties(0.8f)));
        addEntity(body);

        Entity player = new BotCreator(mGame, physicsSystem.getWorld()).createBot("player", w / 4, h - 32 - 16,0);
        addEntity(player);


        // FlipSystem detects player interaction with flip-items like mirrors.
        // Set the player to check interaction with.
        flipSystem.setPlayer(player);

        // Create a emitter
        Entity emitter = new Entity();
        emitter.add(new Transformation(w / 2 + 32, h - 32 - 16, -90));
        emitter.add(new Emitter(mGame.getRenderer(), 300, mGame.getRenderer().mFuzzyTexture, 3, 100, new float[]{180f / 255, 80f / 255, 10f / 255, 1}, new float[]{0, 0, 0, 0}));
        addEntity(emitter);

        Emitter e = emitter.get(Emitter.class);
        e.var_startColor[3] = 0.3f;
        e.size = 30;
        e.var_size = 5;
        e.var_angle = 90;
        e.speed = 10;
        e.var_speed = 5;
        e.accel_x = 20;
        e.additiveBlend = true;

        // Create some few random objects to blow with the wind
        // TODO

        // Create a wind source
        /*Entity windSource = new Entity();
        windSource.add(new Transformation(w, h/2-32, 180));
        windSource.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.STATIC, windSource, null, null));
        windSource.add(new WindSource(100, 1024, 64));

        addEntity(windSource);*/


        // Create a rope
        //GLSprite segmentSprite = new GLSprite(mGame.getRenderer(), null, new float[]{0.5f, 0.5f, 0.5f, 1.0f}, 4, 5);
        GLSprite segmentSprite = new GLSprite(mGame.getRenderer(), mGame.getRenderer().addTexture(R.drawable.rope), 6, 5);
        Entity ropeEntity = new Entity();
        ropeEntity.add(new Rope(w-128, h/2+32, 8, 4, 4, platform.get(PhysicsBody.class).body, null));
        Sprite segSprite = ropeEntity.get(Rope.class).segmentSprite = new Sprite(segmentSprite);
        segSprite.spriteSheetData = new Sprite.SpriteSheetData();
        segSprite.spriteSheetData.numCols = 5;
        segSprite.spriteSheetData.numRows = 1;
        segSprite.spriteSheetData.imgWidth = 6;
        segSprite.spriteSheetData.imgHeight = 5;
        segSprite.spriteSheetData.animationSpeed = 0;

        Rope.BurnData burnData = ropeEntity.get(Rope.class).burn();
        Sprite burnSegSprite = burnData.burningSegmentSprite = new Sprite(segmentSprite);
        burnData.timeToBurn = 1f;

        burnSegSprite.spriteSheetData = new Sprite.SpriteSheetData();
        burnSegSprite.spriteSheetData.numCols = 5;
        burnSegSprite.spriteSheetData.numRows = 1;
        burnSegSprite.spriteSheetData.imgWidth = 6;
        burnSegSprite.spriteSheetData.imgHeight = 5;
        burnSegSprite.spriteSheetData.animationSpeed = 4;   // 1 second to burn, 4 frames on image sheet, hence 4 fps
        burnSegSprite.spriteSheetData.loop = false;

        
        addEntity(ropeEntity);
    }

    // Uncomment following to display FPS on logcat
    /*@Override
    public void onDraw() {
        Log.d("FPS", mGame.getTimer().getFPS() + "");
    }*/
}
