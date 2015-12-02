package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.toggle.katana2d.Background;
import com.toggle.katana2d.Emitter;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;

public class TestLevel extends Level {

    public TestLevel(Game game) {
        super(game, R.raw.flipped);

        int world1 = addWorld("world1", 0);
        ///*int world2 = */addWorld("world2", 180);    // flipped world

        changeWorld(world1, null);
    }

    @Override
    public void onWorldInitialized(World world) {

        Entity bk1 = new Entity();
        bk1.add(new Background(mGame.getRenderer().addTexture(R.drawable.main_background, 640*1.2f, 400*1.2f), 98));
        world.addEntity(bk1);

        Entity bk2 = new Entity();
        bk2.add(new Background(mGame.getRenderer().addTexture(R.drawable.hills, 1034, 640, 2, 1), 75));
        world.addEntity(bk2);

        Entity bk3 = new Entity();
        bk3.add(new Background(mGame.getRenderer().addTexture(R.drawable.trees, 1955, 640, 2, 1), 55));
        world.addEntity(bk3);

        Entity bk3a = new Entity();
        bk3a.add(new Background(mGame.getRenderer().addTexture(R.drawable.fore2, 1500, 640, 2, 1), 44));
        world.addEntity(bk3a);
        bk3a.get(Background.class).mTexture.color = new float[]{0.3f,0.3f,0.3f,1};

        Entity bk4 = new Entity();
        bk4.add(new Background(mGame.getRenderer().addTexture(R.drawable.path, 940, 640, 8, 1), 0));
        //bk4.add(new Background(mGame.getRenderer().addTexture(R.drawable.pa1th, 940*8, 640), 0));
        world.addEntity(bk4);

        /*Entity bk5 = new Entity();
        //bk5.add(new Background(mGame.getRenderer().addTexture(R.drawable.strokes, 940, 640, 8, 1), -3));
        bk5.add(new Sprite(mGame.getRenderer().addTexture(R.drawable.strokes, 940, 640, 8, 1), -3));
        bk5.add(new Transformation(940/2, 640/2, 0));
        world.addEntity(bk5);*/



        // A stick
        Entity stick = new Entity();
        stick.add(new Sprite(mGame.getRenderer().addTexture(new float[]{1,1,0,1}, 16, 4), -1.6f));
        stick.add(new Transformation(500, 500, 0));
        stick.add(new PhysicsBody(world.physicsWorld, BodyDef.BodyType.DynamicBody, stick, new PhysicsBody.Properties(1, 0.2f, 0.1f)));

        Carriable c = new Carriable();
        c.position = new Vector2(156, 136);
        c.angle = -30;
        for (int i=0; i<8; ++i) {
            c.positions.add(new Vector2(1628 - 1500, 256));
            c.angles.add(0f);
        }
        c.positions.add(new Vector2(128, 5128-266));
        c.angles.add(10f);
        c.positions.add(new Vector2(128, 5128-266));
        c.angles.add(10f);
        c.positions.add(new Vector2(638-500, 488-266));
        c.angles.add(15f);
        c.positions.add(new Vector2(890-750, 474-266));
        c.angles.add(10f);
        c.positions.add(new Vector2(1126-1000, 456-266));
        c.angles.add(-7f);
        c.positions.add(new Vector2(1368-1250, 440-266));
        c.angles.add(-10f);
        c.positions.add(new Vector2(1368-1250, 440-266));
        c.angles.add(-20f);
        c.positions.add(new Vector2(1368-1250, 440-266));
        c.angles.add(-20f);

        c.position.x *= 24f/143; c.position.y *= 48f/274;
        c.position.x -= 12; c.position.y -= 24;

        float xFactor = 48f/250;
        float yFactor = 48f/266;

        for (int i=0; i<16; ++i) {
            c.positions.get(i).x *= xFactor;
            c.positions.get(i).y *= yFactor;
            c.positions.get(i).x -= 12;
            c.positions.get(i).y -= 24;
        }


        stick.add(c);

        Entity emitter = new Entity();
        emitter.add(new Transformation(0,0,-90));
        emitter.add(new Emitter(mGame.getRenderer(), 300, mGame.getRenderer().mFuzzyTextureId, 3, 100, new float[]{180f / 255, 80f / 255, 10f / 255, 1}, new float[]{0, 0, 0, 0}));
        Emitter e = emitter.get(Emitter.class);
        e.var_startColor[3] = 0.3f;
        e.size = 16;
        e.var_size = 5;
        e.var_angle = 70;
        e.speed = 10;
        e.var_speed = 5;
        e.accel_x = 20;
        e.additiveBlend = true;
        world.addEntity(emitter);

        stick.add(new Burner(emitter));

        world.addEntity(stick);
    }
}
