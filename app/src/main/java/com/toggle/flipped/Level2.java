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

public class Level2 extends Level {

    public Level2(Game game, Listener listener) {
        super(game, R.raw.level2, listener);
    }

    private List<Texture> mTextures = new ArrayList<>();

    @Override
    public void load() {
        int world1 = addWorld("world1", 0, 3910, 640);
        ///*int world2 = */addWorld("world2", 180, 7520, 640);    // flipped world

        changeWorld(world1, null);
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

       /* Entity bk2 = new Entity();
        bk2.add(new Background(mGame.getRenderer().addTexture(R.drawable.level1_hills, 1034, 640, 2, 1), 75));
        world.addEntity(bk2);*/

        Entity bk3 = new Entity();
        tex = mGame.getRenderer().addTexture(R.drawable.level2_gravestones, 1955, 640, 2, 1);
        bk3.add(new Background(tex, 55));
        world.addEntity(bk3);
        mTextures.add(tex);

        Entity bk4 = new Entity();
        tex = mGame.getRenderer().addTexture(R.drawable.level2_path_glow, 1955, 640, 2, 1);
        bk4.add(new Background(tex, 0));
        world.addEntity(bk4);
        mTextures.add(tex);

        /*Entity bk5 = new Entity();
        tex = mGame.getRenderer().addTexture(R.drawable.level2_grass, 1955, 640, 2, 1);
        bk5.add(new Sprite(tex, -3));
        bk5.add(new Transformation(tex.width/2, tex.height/2, 0));
        world.addEntity(bk5);
        mTextures.add(tex);*/
    }
}