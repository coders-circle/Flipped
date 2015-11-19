package com.toggle.flipped;

import com.toggle.katana2d.Game;

public class TestLevel extends Level {

    public TestLevel(Game game) {
        super(game, R.raw.test2);

        int world1 = addWorld("world1", 0);
        int world2 = addWorld("world2", 180);    // flipped world

        changeWorld(world1);
    }
}
