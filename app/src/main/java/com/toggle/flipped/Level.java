package com.toggle.flipped;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Manager;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.Utilities;
import com.toggle.katana2d.physics.PhysicsBody;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.BodyType;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Level implements CustomLoader {

    private LevelLoader mLevelLoader;
    private Game mGame;
    public Manager<Entity> standardEntities;  // Some standard entities that need to be recorded

    public Level(Game game, int levelFileId) {
        String level = Utilities.getRawFileText(game.getActivity(), levelFileId);

        mLevelLoader = new LevelLoader();
        mLevelLoader.load(game, level, this);
        mGame = game;
    }

    private List<World> mWorlds = new ArrayList<>();
    private World mActiveWorld = null;

    public int addWorld(String worldName) {
        World newWorld = new World(this, mLevelLoader, worldName);
        mWorlds.add(newWorld);
        newWorld.mSceneId = mGame.addScene(newWorld);
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

    // When level exits, we may unload resources specific to that level
    public void unloadResources() {

    }

    public void changeWorld(int nextWorldId) {
        if (mActiveWorld != null)
            mActiveWorld.unload();

        setActiveWorld(nextWorldId);
        mGame.setActiveScene(mActiveWorld.mSceneId);
        mActiveWorld.load();
    }


    @Override
    public boolean loadSprite(Game game, String spriteName, JSONObject sprite) {
        spriteName = spriteName.toLowerCase();
        switch (spriteName) {
            case "player":
                return true;
        }
        return false;
    }

    @Override
    public boolean loadEntity(Scene scene, org.jbox2d.dynamics.World world, String entityName, JSONObject entity) {
        try {
            entityName = entityName.toLowerCase();
            JSONObject components = entity.getJSONObject("components");
            switch (entityName) {
                case "player":
                    JSONObject transformation = components.getJSONObject("Transformation");
                    Entity player = new BotCreator(scene.getGame(), world).createBot("player",
                            (float) transformation.getDouble("Translate-X"),
                            (float) transformation.getDouble("Translate-Y"), (float) transformation.getDouble("Angle"));
                    player.add(new Player());
                    scene.addEntity(player);

                    standardEntities.add("player", player);
                    return true;

                case "ground":
                    Entity ground = new Entity();
                    Shape shape = com.toggle.flipped.Utilities.createChainShape(components.getJSONObject("Path").getString("Points"));
                    ground.add(new Transformation(0, 0, 0));
                    ground.add(new PhysicsBody(world, BodyType.STATIC, ground, shape, new PhysicsBody.Properties(0)));
                    scene.addEntity(ground);
                    return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
