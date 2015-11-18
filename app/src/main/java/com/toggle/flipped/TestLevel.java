package com.toggle.flipped;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Utilities;
import com.toggle.katana2d.physics.PhysicsSystem;

import org.jbox2d.dynamics.*;
import org.json.JSONObject;

public class TestLevel extends Level {

    public TestLevel(Game game) {
        super(game, R.raw.test2);
        changeWorld(addWorld("world1"));
    }
}
