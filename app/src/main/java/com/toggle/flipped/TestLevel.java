package com.toggle.flipped;

import com.toggle.katana2d.Background;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;

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

        Entity bk5 = new Entity();
        //bk5.add(new Background(mGame.getRenderer().addTexture(R.drawable.strokes, 940, 640, 8, 1), -3));
        bk5.add(new Sprite(mGame.getRenderer().addTexture(R.drawable.strokes, 940, 640, 8, 1), -3));
        bk5.add(new Transformation(940/2, 640/2, 0));
        world.addEntity(bk5);
    }
}
