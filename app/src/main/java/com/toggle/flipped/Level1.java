package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.toggle.katana2d.Background;
import com.toggle.katana2d.Emitter;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.GLRenderer;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Texture;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

import java.util.ArrayList;
import java.util.List;

public class Level1 extends Level {

    public Level1(Game game, Listener listener) {
        super(game, R.raw.level1, listener);
    }

    private List<Texture> mTextures = new ArrayList<>();

    @Override
    public void load() {
        addWorld("world1", 0, 7520, 640);
        addWorld("world2", 180, 3500, 640);    // flipped world

        changeWorld(0, null);
    }

    @Override
    public void unload() {
        GLRenderer renderer = mGame.getRenderer();
        for (Texture t: mTextures)
            renderer.deleteTexture(t);
        mTextures.clear();
        mWorlds.clear();
        changeWorld(-1, null);
    }

    @Override
    public void onWorldInitialized(World world) {
        Texture tex;

        if (world == mWorlds.get(0)) {
            Entity bk1 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1_back, 640 * 1.2f, 400 * 1.2f);
            bk1.add(new Background(tex, 98));
            world.addEntity(bk1);
            mTextures.add(tex);

            Entity bk2 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1_hills, 1034, 640, 2, 1);
            bk2.add(new Background(tex, 75));
            world.addEntity(bk2);
            mTextures.add(tex);

            Entity bk3 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1_trees, 1955, 640, 2, 1);
            bk3.add(new Background(tex, 55));
            world.addEntity(bk3);
            mTextures.add(tex);

            Entity bk3a = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1_forest, 1500, 640, 2, 1);
            tex.color = new float[]{0.3f, 0.3f, 0.3f, 1};
            bk3a.add(new Background(tex, 44));
            world.addEntity(bk3a);
            mTextures.add(tex);

            Entity bk4 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1_path, 940, 640, 8, 1);
            bk4.add(new Background(tex, 0));
            world.addEntity(bk4);
            mTextures.add(tex);

            /*Entity bk5 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.strokes, 940, 640, 8, 1);
            bk5.add(new Sprite(tex, -3));
            bk5.add(new Transformation(tex.width/2, tex.height/2, 0));
            world.addEntity(bk5);
            mTextures.add(tex);*/

            // A stick
            Entity stick = new Entity();
            stick.add(new Sprite(mGame.getRenderer().addTexture(new float[]{1, 1, 0, 1}, 16, 4), -1.6f));
            stick.add(new Transformation(500, 500, 0));
            PhysicsBody b = new PhysicsBody(world.physicsWorld, BodyDef.BodyType.DynamicBody, stick, new PhysicsBody.Properties(1, 0.2f, 0.1f));
            stick.add(b);

            Filter d = b.body.getFixtureList().get(0).getFilterData();
            d.groupIndex = ExplosionSystem.NON_EXPLOSIVE_GROUP;
            b.body.getFixtureList().get(0).setFilterData(d);

            PolygonShape fireShape = new PolygonShape();
            fireShape.setAsBox(28 * PhysicsSystem.METERS_PER_PIXEL, 28 * PhysicsSystem.METERS_PER_PIXEL, new Vector2(10 * PhysicsSystem.METERS_PER_PIXEL, 0), 0);
            b.createSensor(fireShape);

            Carriable c = new Carriable();
            c.position = new Vector2(156, 136);
            c.angle = -30;
            for (int i = 0; i < 8; ++i) {
                c.positions.add(new Vector2(1628 - 1500, 256));
                c.angles.add(0f);
            }
            c.positions.add(new Vector2(128, 5128 - 266));
            c.angles.add(10f);
            c.positions.add(new Vector2(128, 5128 - 266));
            c.angles.add(10f);
            c.positions.add(new Vector2(638 - 500, 488 - 266));
            c.angles.add(15f);
            c.positions.add(new Vector2(890 - 750, 474 - 266));
            c.angles.add(10f);
            c.positions.add(new Vector2(1126 - 1000, 456 - 266));
            c.angles.add(-7f);
            c.positions.add(new Vector2(1368 - 1250, 440 - 266));
            c.angles.add(-10f);
            c.positions.add(new Vector2(1368 - 1250, 440 - 266));
            c.angles.add(-20f);
            c.positions.add(new Vector2(1368 - 1250, 440 - 266));
            c.angles.add(-20f);

            c.position.x *= 24f / 143;
            c.position.y *= 48f / 274;
            c.position.x -= 12;
            c.position.y -= 24;

            float xFactor = 48f / 250;
            float yFactor = 48f / 266;

            for (int i = 0; i < 16; ++i) {
                c.positions.get(i).x *= xFactor;
                c.positions.get(i).y *= yFactor;
                c.positions.get(i).x -= 12;
                c.positions.get(i).y -= 24;
            }


            stick.add(c);

            Entity emitter = new Entity();
            emitter.add(new Transformation(0, 0, -90));
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
            stick.add(new Fire());

            world.addEntity(stick);
        }
        else if (world == mWorlds.get(1)) {
            Entity bk1 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1a_back, 640 * 1.2f, 400 * 1.2f);
            bk1.add(new Background(tex, 98));
            world.addEntity(bk1);
            mTextures.add(tex);

            Entity bk2 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1a_hills, 875, 640, 2, 1);
            bk2.add(new Background(tex, 75));
            world.addEntity(bk2);
            mTextures.add(tex);

            Entity bk3 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1a_trees, 1000, 640, 2, 1);
            bk3.add(new Background(tex, 55));
            world.addEntity(bk3);
            mTextures.add(tex);

            /*Entity bk3a = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1a_forest, 1500, 640, 2, 1);
            tex.color = new float[]{0.3f, 0.3f, 0.3f, 1};
            bk3a.add(new Background(tex, 44));
            world.addEntity(bk3a);
            mTextures.add(tex);*/

            Entity bk4 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1a_path, 1750, 640, 2, 1);
            bk4.add(new Background(tex, 0));
            world.addEntity(bk4);
            mTextures.add(tex);
        }
    }
}
