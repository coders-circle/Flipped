package com.toggle.flipped;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Manager;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.Utilities;
import com.toggle.katana2d.physics.PhysicsBody;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.BodyType;
import org.json.JSONObject;

public class Level implements CustomLoader {

    protected LevelLoader mLevelLoader;
    protected Game mGame;
    public Manager<Entity> standardEntities = new Manager<>();  // Some standard entities that need to be recorded

    public Level(Game game, int levelFileId) {
        mGame = game;
        String level = Utilities.getRawFileText(game.getActivity(), levelFileId);
        mLevelLoader = new LevelLoader(level);
        mLevelLoader.load(mGame, this);
    }

    protected Manager<World> mWorlds = new Manager<>();
    protected World mActiveWorld = null;

    public int addWorld(String worldName, float angle) {
        World newWorld = new World(this, mLevelLoader, worldName);
        int id = mWorlds.add(worldName, newWorld);
        newWorld.mSceneId = mGame.addScene(newWorld);
        newWorld.mAngle = angle;
        return id;
    }

    public void setActiveWorld(int index) {
        if (index < 0 || index >= mWorlds.size())
            mActiveWorld = null;
        else {
            mActiveWorld = mWorlds.get(index);
            mGame.setActiveScene(mActiveWorld.mSceneId);
        }
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
        mActiveWorld.load();
    }

    public void changeWorld(String nextWorld) {
        changeWorld(mWorlds.getId(nextWorld));
    }

    @Override
    public boolean loadSprite(Game game, String spriteName, JSONObject sprite) {
        spriteName = spriteName.toLowerCase();
        switch (spriteName) {
            case "player":
                return true;
            case "block":
                if (!game.textureManager.has(spriteName))
                    game.textureManager.add(spriteName, game.getRenderer().addTexture(
                            new float[]{0.2f, 0.2f, 0.2f, 1},
                            (float)sprite.optDouble("width", 32), (float)sprite.optDouble("height", 32)
                    ));
                return true;
            case "mirror":
                if (!game.textureManager.has(spriteName))
                    game.textureManager.add(spriteName, game.getRenderer().addTexture(
                            new float[]{0.0f, 0.0f, 0.7f, 1}, 32, 6
                    ));
                return true;
        }
        return false;
    }

    @Override
    public boolean loadEntity(Scene scene, org.jbox2d.dynamics.World world, String entityName, JSONObject entity) {
        try {
            entityName = entityName.toLowerCase();
            JSONObject components = entity.getJSONObject("components");
            JSONObject transformation;

            switch (entityName) {
                case "player":
                    transformation = components.getJSONObject("Transformation");
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

                default:
                    if (components.has("Mirror")) {
                        Entity mirror = new Entity();
                        transformation = components.getJSONObject("Transformation");
                        mirror.add(new Transformation((float) transformation.getDouble("Translate-X"),
                                (float) transformation.getDouble("Translate-Y"), (float) transformation.getDouble("Angle")));
                        mirror.add(new Sprite(mGame.textureManager.get("mirror")));
                        mirror.add(new PhysicsBody(world, BodyType.STATIC, mirror, new PhysicsBody.Properties(true)));
                        mirror.add(new FlipSystem.FlipItem(FlipSystem.FlipItem.FlipItemType.MIRROR));

                        FlipSystem.Mirror mrr;
                        mirror.get(FlipSystem.FlipItem.class).data = mrr = new FlipSystem.Mirror();
                        mrr.nextWorld = components.getJSONObject("Mirror").getString("Next world");

                        scene.addEntity(mirror);
                        return true;
                    }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
