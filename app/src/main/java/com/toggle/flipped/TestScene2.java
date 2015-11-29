package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.toggle.katana2d.Background;
import com.toggle.katana2d.BackgroundSystem;
import com.toggle.katana2d.Emitter;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.ParticleSystem;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

import java.util.ArrayList;
import java.util.List;

public class TestScene2 extends Scene {

    @Override
    public void onInit() {
        PhysicsSystem physicsSystem = new PhysicsSystem();
        //FlipSystem flipSystem = new FlipSystem(mGame.getRenderer().getCamera());
        ParticleSystem particleSystem = new ParticleSystem();

        // Add the systems
        mSystems.add(new BackgroundSystem(mGame.getRenderer()));   // Background system must prepend RenderSystem
        mSystems.add(new RenderSystem(mGame.getRenderer()));
        mSystems.add(physicsSystem);
        mSystems.add(particleSystem);

        mSystems.add(new BotControlSystem());
        mSystems.add(new PlayerInputSystem(mGame));
        //mSystems.add(flipSystem);
        mSystems.add(new WindSystem());
        mSystems.add(new RopeSystem(physicsSystem.getWorld(), mGame.getRenderer()));
        mSystems.add(new PlatformSystem());

        // Some sprites which are just colored boxes
        mGame.getRenderer().setBackgroundColor(0, 0, 0);
        float w = mGame.getRenderer().width, h = mGame.getRenderer().height;

        int spr0 = mGame.textureManager.add(
                mGame.getRenderer().addTexture(new float[]{0.7f, 0.7f, 0.7f, 1}, 32, 32)
        );
        int spr2 = mGame.textureManager.add(
                mGame.getRenderer().addTexture(new float[]{0.2f, 0.2f, 0.2f, 1.0f}, w, 32)
        );
        int spr3 = mGame.textureManager.add(
                mGame.getRenderer().addTexture(new float[]{0.2f, 0.2f, 0.2f, 1.0f}, w * 2, 32)
        );
        int spr4 = mGame.textureManager.add(
                mGame.getRenderer().addTexture(new float[]{0.5f, 0.5f, 0.0f, 1.0f}, 32, 4)
        );

        // Add some entities

        Entity ground = new Entity();
        ground.add(new Transformation(w, h - 16, 0));
        ground.add(new Sprite(mGame.textureManager.get(spr3), 3));
        ground.add(new PhysicsBody(physicsSystem.getWorld(), BodyDef.BodyType.StaticBody, ground, new PhysicsBody.Properties(0)));
        addEntity(ground);

        Entity platform = new Entity();
        platform.add(new Transformation(w, h / 2 + 16, 0));
        platform.add(new Sprite(mGame.textureManager.get(spr2), 3));
        platform.add(new PhysicsBody(physicsSystem.getWorld(), BodyDef.BodyType.StaticBody, platform, new PhysicsBody.Properties(0)));
        //platform.add(new PlatformSystem.OneWayPlatform());
        addEntity(platform);

        /*Entity mirror1 = new Entity();
        mirror1.add(new Transformation(w, h / 2 + 2, 0));
        mirror1.add(new Sprite(mGame.textureManager.get(spr4)));
        mirror1.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.STATIC, mirror1, new PhysicsBody.Properties(true)));
        mirror1.add(new FlipSystem.FlipItem(FlipSystem.FlipItem.FlipItemType.MIRROR));
        addEntity(mirror1);

        mirror1.get(FlipSystem.FlipItem.class).targetAngle = 180;

        Entity mirror2 = new Entity();
        mirror2.add(new Transformation(w, h - 32 + 2, 0));
        mirror2.add(new Sprite(mGame.textureManager.get(spr4)));
        mirror2.add(new PhysicsBody(physicsSystem.getWorld(), BodyType.STATIC, mirror2, new PhysicsBody.Properties(true)));
        mirror2.add(new FlipSystem.FlipItem(FlipSystem.FlipItem.FlipItemType.MIRROR));
        addEntity(mirror2);

        mirror2.get(FlipSystem.FlipItem.class).targetAngle = 0;*/

        Entity body = new Entity();
        body.add(new Transformation(w / 2-64, h - 32 - 16, 0));
        body.add(new Sprite(mGame.textureManager.get(spr0), 0));
        body.add(new PhysicsBody(physicsSystem.getWorld(), BodyDef.BodyType.DynamicBody, body, new PhysicsBody.Properties(0.8f)));
        addEntity(body);

        // Use botceator to create a bot from "bot_player.json" file
        Entity player = new Entity();
        new BotCreator(mGame, physicsSystem.getWorld()).createBot(player, "player", w / 4, h - 32 - 16, 0);
        player.add(new Player());
        addEntity(player);


        // FlipSystem detects player interaction with flip-items like mirrors.
        // Set the player to check interaction with.
        //flipSystem.setPlayer(player);

        // Create a emitter
        Entity emitter = new Entity();
        emitter.add(new Transformation(w / 2 + 32, h - 32 - 16, -90));
        emitter.add(new Emitter(mGame.getRenderer(), 300, mGame.getRenderer().mFuzzyTextureId, 3, 100, new float[]{180f / 255, 80f / 255, 10f / 255, 1}, new float[]{0, 0, 0, 0}));
        addEntity(emitter);

        Emitter e = emitter.get(Emitter.class);
        e.var_startColor[3] = 0.3f;
        e.size = 20;
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

        addEntity(windSource);
*/

        // Create a rope

        Entity ropeEntity = new Entity();
        List<Vector2> ropePath = new ArrayList<>();
        ropePath.add(new Vector2(w-128, h/2+32));
        ropePath.add(new Vector2(w-128, h/2+32 + Rope.STANDARD_SEGMENT_LENGTH * 4));
        ropeEntity.add(new Rope(ropePath, Rope.STANDARD_SEGMENT_THICKNESS, Rope.STANDARD_SEGMENT_LENGTH, platform, null));
        ropeEntity.get(Rope.class).segmentSprite = new Sprite(mGame.textureManager.get("rope"), 0);
        addEntity(ropeEntity);


        Entity bk1 = new Entity();
        bk1.add(new Background(mGame.getRenderer().addTexture(R.drawable.main_background, 640, 400), 100));
        addEntity(bk1);

        Entity bk2 = new Entity();
        bk2.add(new Background(mGame.getRenderer().addTexture(R.drawable.hills, 640, 480), 50));
        addEntity(bk2);

        Entity bk3 = new Entity();
        bk3.add(new Background(mGame.getRenderer().addTexture(R.drawable.trees, 640, 480), 30));
        addEntity(bk3);

        Entity bk4 = new Entity();
        bk4.add(new Background(mGame.getRenderer().addTexture(R.drawable.pa1th, 640, 480), 0));
        addEntity(bk4);
    }
    //Font font;

    @Override
    public void onDraw() {
        // Uncomment following to display FPS on logcat
        //Log.d("FPS", mGame.getTimer().getFPS() + "");
        /*font.setColor(new float[]{1,0,0,1});
        font.draw("hello\nworld", 30, 30, 0, 1, 1);*/
    }
}
