package com.toggle.flipped;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Utilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Level implements CustomLoader {

    private CustomLoader mCommonLoader;
    private LevelLoader mLevelLoader;

    public Level(Game game, CustomLoader commonLoader, int levelFileId) {
        mCommonLoader = commonLoader;
        String level = Utilities.getRawFileText(game.getActivity(), levelFileId);

        mLevelLoader = new LevelLoader();
        mLevelLoader.load(game, level, this);
    }

    private List<World> mWorlds = new ArrayList<>();
    private World mActiveWorld = null;

    public int addWorld(String worldName) {
        mWorlds.add(new World(mLevelLoader, worldName));
        return mWorlds.size()-1;
    }

    public void setActiveWorld(int index) {
        if (index < 0 || index >= mWorlds.size())
            mActiveWorld = null;
        else
            mActiveWorld = mWorlds.get(index);
    }

    public World getActiveWorld() {
        return mActiveWorld;
    }

    @Override
    public boolean loadSprite(Game game, String spriteName, JSONObject sprite) {
        if (mCommonLoader.loadSprite(game, spriteName, sprite))
            return true;
        return false;
    }

    @Override
    public boolean loadEntity(Scene scene, org.jbox2d.dynamics.World world, String entityName, JSONObject entity) {
        if (mCommonLoader.loadEntity(scene, world, entityName, entity))
            return true;
        return false;
    }
}
