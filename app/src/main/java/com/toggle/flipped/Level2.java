package com.toggle.flipped;

import android.app.Dialog;
import android.util.Log;

import com.toggle.katana2d.Background;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.GLRenderer;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Texture;
import com.toggle.katana2d.Transformation;

import java.util.ArrayList;
import java.util.List;

public class Level2 extends Level {

    public Level2(Game game, Listener listener) {
        super(game, R.raw.level2, listener);
    }

    private List<Texture> mTextures = new ArrayList<>();

    @Override
    public void load() {
        mGame.getActivity().handler.sendEmptyMessage(1);

        int world1 = addWorld("world1", 0, 7520, 640);
        ///*int world2 = */addWorld("world2", 180, 7520, 640);    // flipped world

        changeWorld(world1, null);

        //loader_dialog.dismiss();
        mGame.getActivity().handler.sendEmptyMessage(2);
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

        Entity bk1 = new Entity();
        tex = mGame.getRenderer().addTexture(R.drawable.level2_back, 683*1.2f, 320*1.2f);
        bk1.add(new Background(tex, 98));
        world.addEntity(bk1);
        mTextures.add(tex);

        Entity bk2 = new Entity();
        tex = mGame.getRenderer().addTexture(R.drawable.level2_hills, 1336, 640, 2, 1);
        bk2.add(new Background(tex, 75));
        world.addEntity(bk2);
        mTextures.add(tex);

        /*Entity bk2aa = new Entity();
        tex = mGame.getRenderer().addTexture(R.drawable.level2_hills_fog, 1342, 640, 2, 1);
        bk2aa.add(new Background(tex, 74));
        world.addEntity(bk2aa);
        mTextures.add(tex);*/

        Entity bk2a = new Entity();
        tex = mGame.getRenderer().addTexture(R.drawable.level2_fences, 1094, 640, 4, 1);
        bk2a.add(new Background(tex, 55));
        world.addEntity(bk2a);
        mTextures.add(tex);

        Entity bk3 = new Entity();
        tex = mGame.getRenderer().addTexture(R.drawable.level2_gravestones, 1554, 640, 4, 1);
        bk3.add(new Background(tex, 20));
        world.addEntity(bk3);
        mTextures.add(tex);

        Entity bk4 = new Entity();
        tex = mGame.getRenderer().addTexture(R.drawable.level2_path, 1880, 640, 4, 1);
        bk4.add(new Sprite(tex, 0));
        bk4.add(new Transformation(tex.width/2, tex.height/2, 0));
        world.addEntity(bk4);
        mTextures.add(tex);

        /*Entity bk5 = new Entity();
        tex = mGame.getRenderer().addTexture(R.drawable.level2_grass, 1955, 640, 2, 1);
        bk5.add(new Sprite(tex, -3));
        bk5.add(new Transformation(tex.width/2, tex.height/2, 0));
        world.addEntity(bk5);
        mTextures.add(tex);*/

        /*Entity lever = new Entity();
        lever.add(new Sprite(mGame.getRenderer().addTexture(new float[]{1,0,0,1,}, 32f, 8f), -1));
        lever.add(new Transformation())
        world.addEntity(lever);*/
    }
}
