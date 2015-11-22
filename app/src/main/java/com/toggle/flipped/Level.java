package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Manager;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsUtilities;

import org.json.JSONObject;

import java.util.List;

public class Level implements CustomLoader {

    public LevelLoader levelLoader;
    protected Game mGame;

    public Level(Game game, int levelFileId) {
        mGame = game;
        String level = com.toggle.katana2d.Utilities.getRawFileText(game.getActivity(), levelFileId);
        levelLoader = new LevelLoader(level);
        levelLoader.load(mGame, this);
    }

    protected Manager<World> mWorlds = new Manager<>();
    protected World mActiveWorld = null;

    public int addWorld(String worldName, float angle) {
        World newWorld = new World(this, levelLoader, worldName);
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
    public Entity loadEntity(Scene scene, com.badlogic.gdx.physics.box2d.World world, String entityName, JSONObject entity) {
        try {
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

                    //standardEntities.add("player", player);
                    return player;

                case "ground":
                    Entity ground = new Entity();
                    Shape shape = Utilities.createChainShape(components.getJSONObject("Path").getString("Points"));
                    ground.add(new Transformation(0, 0, 0));
                    ground.add(new PhysicsBody(world, BodyDef.BodyType.StaticBody, ground, shape, new PhysicsBody.Properties(0)));
                    scene.addEntity(ground);
                    return ground;

                default:
                    if (components.has("Mirror")) {
                        Entity mirror = new Entity();
                        transformation = components.getJSONObject("Transformation");
                        mirror.add(new Transformation((float) transformation.getDouble("Translate-X"),
                                (float) transformation.getDouble("Translate-Y"), (float) transformation.getDouble("Angle")));
                        mirror.add(new Sprite(mGame.textureManager.get("mirror"), -1));
                        mirror.add(new PhysicsBody(world, BodyDef.BodyType.StaticBody, mirror, new PhysicsBody.Properties(true)));
                        mirror.add(new FlipSystem.FlipItem(FlipSystem.FlipItem.FlipItemType.MIRROR));

                        FlipSystem.Mirror mrr;
                        mirror.get(FlipSystem.FlipItem.class).data = mrr = new FlipSystem.Mirror();
                        mrr.nextWorld = components.getJSONObject("Mirror").getString("Next world");

                        scene.addEntity(mirror);
                        return mirror;
                    }
                    else if (components.has("Rope") && components.has("Path")) {
                        Entity rope = new Entity();

                        JSONObject r = components.getJSONObject("Rope");
                        Body startBody = levelLoader.mEntities.get(r.getString("Start Body")).get(PhysicsBody.class).body;
                        Body endBody = null;
                        if (r.getString("End Body").trim().length() > 0)
                            endBody = levelLoader.mEntities.get(r.getString("End Body")).get(PhysicsBody.class).body;

                        List<Vector2> path = PhysicsUtilities.parsePoints(components.getJSONObject("Path").getString("Points"), false, 0, 0);
                        rope.add(new Rope(path, Rope.STANDARD_SEGMENT_THICKNESS, Rope.STANDARD_SEGMENT_LENGTH,
                                startBody, endBody));

                        Sprite segSprite = rope.get(Rope.class).segmentSprite = new Sprite(mGame.textureManager.get("rope"), 0);
                        scene.addEntity(rope);
                        return rope;
                    }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
