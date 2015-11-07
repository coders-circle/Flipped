package com.toggle.flipped;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Utilities;
import com.toggle.katana2d.physics.PhysicsSystem;

import org.json.JSONObject;

public class LevelTestScene extends Scene implements CustomLoader {

    @Override
    public void onInit() {
        PhysicsSystem physicsSystem = new PhysicsSystem();

        // Add the systems
        mSystems.add(new RenderSystem());
        mSystems.add(physicsSystem);

        String level = Utilities.getRawFileText(mGame.getActivity(), R.raw.basketball_test);
        LevelLoader loader = new LevelLoader();
        loader.load(mGame, level, this);

        loader.loadWorld("World1", this, physicsSystem.getWorld());
    }

    @Override
    public boolean loadSprite(Game game, String spriteName, JSONObject sprite) {
        return false;
    }

    @Override
    public boolean loadEntity(Scene scene, String entityName, JSONObject entity) {
        return false;
    }
}
