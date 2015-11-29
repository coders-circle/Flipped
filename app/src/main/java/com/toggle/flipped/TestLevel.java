package com.toggle.flipped;

import com.toggle.katana2d.Background;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;

public class TestLevel extends Level {

    public TestLevel(Game game) {
        super(game, R.raw.flipped);

        int world1 = addWorld("world1", 0);
        ///*int world2 = */addWorld("world2", 180);    // flipped world

        changeWorld(world1);
    }

    @Override
    public void onWorldInitialized(World world) {

        Entity bk1 = new Entity();
        bk1.add(new Background(mGame.getRenderer().addTexture(R.drawable.main_background, 640, 400), 100));
        world.addEntity(bk1);

        Entity bk2 = new Entity();
        bk2.add(new Background(mGame.getRenderer().addTexture(R.drawable.hills, 640, 480), 50));
        world.addEntity(bk2);

        Entity bk3 = new Entity();
        bk3.add(new Background(mGame.getRenderer().addTexture(R.drawable.trees, 640, 480), 30));
        world.addEntity(bk3);

        Entity bk4 = new Entity();
        //bk4.add(new Background(mGame.getRenderer().addTexture(R.drawable.path, 940, 640, 8, 1), 0));
        bk4.add(new Background(mGame.getRenderer().addTexture(R.drawable.pa1th, 940*8, 640), 0));
        world.addEntity(bk4);
    }
}
