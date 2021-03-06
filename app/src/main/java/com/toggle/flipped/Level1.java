package com.toggle.flipped;

import com.toggle.katana2d.Background;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.GLRenderer;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Texture;
import com.toggle.katana2d.Transformation;

import java.util.ArrayList;
import java.util.List;

public class Level1 extends Level {

    public Level1(Game game, Listener listener) {
        super(game, R.raw.level1, listener);
    }

    private List<Texture> mTextures = new ArrayList<>();

    @Override
    public void load() {
        //mGame.getActivity().handler.sendEmptyMessage(1);

        SplashScreen sc = mListener.getSplashScreen();
        sc.data.text = "Loading Level";
        sc.data.background = null;
        sc.data.showTime = -1;
        sc.listener = new SplashScreen.Listener() {
            @Override
            public void onShown() {
                addWorld("world1", 0, 7300, 640);
                addWorld("world2", 180, 3500, 640);    // flipped world

                changeWorld(0, null);
            }
        };
        mGame.setActiveScene(sc.sceneId);

        //mGame.getActivity().handler.sendEmptyMessage(2);
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
            // add snow
            Entity snow = new Entity();
            Utilities.createSnow(world, snow, world.getWidth());
            world.addEntity(snow);

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
/*
            Entity fg1 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.hills_fog, 1034, 640, 2, 1);
            fg1.add(new Background(tex, 74));
            world.addEntity(fg1);
            mTextures.add(tex);*/

            Entity bk3 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1_trees, 1955, 640, 2, 1);
            bk3.add(new Background(tex, 55));
            world.addEntity(bk3);
            mTextures.add(tex);


            Entity bk3aa = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.billboard, 320, 240);
            bk3aa.add(new Background(tex, 50, 2800, 190));
            world.addEntity(bk3aa);
            mTextures.add(tex);

            /*Entity fg2 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.tree_fog, 1955, 640, 2, 1);
            fg2.add(new Background(tex, 54));
            world.addEntity(fg2);
            mTextures.add(tex);*/

            Entity bk3a = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1_forest, 1500, 640, 2, 1);
            tex.color = new float[]{0.3f, 0.3f, 0.3f, 1};
            bk3a.add(new Background(tex, 44));
            world.addEntity(bk3a);
            mTextures.add(tex);

            /*Entity bk4 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1_path, 1825, 640, 4, 1);
            bk4.add(new Background(tex, 0));
            world.addEntity(bk4);
            mTextures.add(tex);*/
            Entity bk4 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1_path, 1825, 640, 4, 1);
            bk4.add(new Sprite(tex, 0));
            bk4.add(new Transformation(tex.width/2, tex.height/2, 0));
            world.addEntity(bk4);
            mTextures.add(tex);

            Entity bk5 = new Entity();
            tex = mGame.getRenderer().addTexture(R.drawable.level1_snow, 1825, 640, 4, 1);
            bk5.add(new Sprite(tex, -3));
            bk5.add(new Transformation(tex.width/2, tex.height/2, 0));
            world.addEntity(bk5);
            mTextures.add(tex);
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
