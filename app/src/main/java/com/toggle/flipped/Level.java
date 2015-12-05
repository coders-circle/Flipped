package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Manager;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;
import com.toggle.katana2d.physics.PhysicsUtilities;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Level implements CustomLoader, World.WorldEventListener {

    public LevelLoader levelLoader;
    protected Game mGame;
    public List<Checkpoint> checkpoints = new ArrayList<>();
    public HashMap<String, Entity> mirrors = new HashMap<>();

    public interface Listener {
        void onLevelComplete(Level level);
    }
    private Listener mListener;

    public Level(Game game, int levelFileId, Listener listener) {
        mGame = game;
        mListener = listener;
        String level = com.toggle.katana2d.Utilities.getRawFileText(game.getActivity(), levelFileId);
        levelLoader = new LevelLoader(level);
        levelLoader.load(mGame, this);
    }

    public void complete() {
        mListener.onLevelComplete(this);
    }

    protected Manager<World> mWorlds = new Manager<>();
    protected World mActiveWorld = null;

    public int addWorld(String worldName, float angle, float width, float height) {
        World newWorld = new World(this, levelLoader, worldName, width, height);
        int id = mWorlds.add(worldName, newWorld);
        newWorld.mSceneId = mGame.addScene(newWorld);
        newWorld.mAngle = angle;
        return id;
    }

    public void setActiveWorld(int index) {
        if (index < 0 || index >= mWorlds.size()) {
            mActiveWorld = null;
            mGame.setActiveScene(-1);
        }
        else {
            mActiveWorld = mWorlds.get(index);
            mGame.setActiveScene(mActiveWorld.mSceneId);
        }
    }

/*    public World getActiveWorld() {
        return mActiveWorld;
    }*/

    public void restart() {
        unload();
        load();
    }

    // load a level
    public void load() {

    }

    // When level exits, we may unload resources specific to that level
    public void unload() {

    }

    public void changeWorld(int nextWorldId, Entity entryMirror) {
        if (mActiveWorld != null)
            mActiveWorld.unload();

        setActiveWorld(nextWorldId);
        if (mActiveWorld != null)
            mActiveWorld.load(entryMirror);
    }

    public void changeWorld(String nextWorld, Entity entryMirror) {
        changeWorld(mWorlds.getId(nextWorld), entryMirror);
    }

    public void changeWorld(FlipSystem.Mirror m) {
        if (m.nextWorld.equals("next level"))
            complete();
        else if (!m.nextWorld.equals("") && mWorlds.has(m.nextWorld))
            changeWorld(m.nextWorld, mirrors.get(m.exitMirror));
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
            case "hanger":
                /*if (!game.textureManager.has(spriteName))
                    game.textureManager.add(spriteName, game.getRenderer().addTexture(
                            new float[]{0.0f, 0.0f, 0.7f, 1}, 32, 16
                ));*/
                return true;
            case "lever":
                if (!game.textureManager.has(spriteName)) {
                    game.textureManager.add(spriteName, game.getRenderer().addTexture(
                           R.drawable.lever, 32, 8
                    ));
                    game.textureManager.get(spriteName).originX = 1;
                }
                return true;
            case "cross":
                if (!game.textureManager.has(spriteName)) {
                    game.textureManager.add(spriteName, game.getRenderer().addTexture(
                            R.drawable.cross, 247, 275
                    ));
                    game.textureManager.get(spriteName).originY = 1;
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean loadEntity(Entity entity, com.badlogic.gdx.physics.box2d.World world, String entityName, JSONObject entityJson) {
        try {
            JSONObject components = entityJson.getJSONObject("components");
            JSONObject transformation;
            Scene scene = levelLoader.mCurrentScene;

            if (entityName.equals("player")) {
                transformation = components.getJSONObject("Transformation");
                new BotCreator(scene.getGame(), world).createBot(entity, "player",
                        (float) transformation.getDouble("Translate-X"),
                        (float) transformation.getDouble("Translate-Y"), (float) transformation.getDouble("Angle"));
                entity.add(new Player());

                /*Sound s = new Sound();
                entity.add(s);

                s.addSource(mGame.getActivity(), R.raw.bg_music, Sound.AMBIANCE);*/
                return true;
            }
            else if (entityName.startsWith("ground")) {
                Shape shape = Utilities.createChainShape(components.getJSONObject("Path").getString("Points"));
                entity.add(new Transformation(0, 0, 0));
                entity.add(new PhysicsBody(world, BodyDef.BodyType.StaticBody, entity, shape, new PhysicsBody.Properties(0)));
                return true;

            }
            else if (components.has("Mirror")) {
                transformation = components.getJSONObject("Transformation");
                entity.add(new Transformation((float) transformation.getDouble("Translate-X"),
                        (float) transformation.getDouble("Translate-Y"), (float) transformation.getDouble("Angle")));
                entity.add(new Sprite(mGame.textureManager.get("mirror"), -1));
                entity.add(new PhysicsBody(world, BodyDef.BodyType.StaticBody, entity, new PhysicsBody.Properties(true)));
                entity.add(new FlipSystem.Mirror());

                FlipSystem.Mirror mrr = entity.get(FlipSystem.Mirror.class);
                JSONObject mirrorJson = components.getJSONObject("Mirror");
                mrr.nextWorld = mirrorJson.getString("Next world");
                mrr.exitMirror = mirrorJson.getString("Exit mirror");

                mirrors.put(entityName, entity);
                return true;
            }
            else if (components.has("Rope") && components.has("Path")) {
                JSONObject r = components.getJSONObject("Rope");
                Entity startBody = levelLoader.mCurrentEntities.get(r.getString("Start Body"));
                Entity endBody = null;
                if (r.getString("End Body").trim().length() > 0)
                    endBody = levelLoader.mCurrentEntities.get(r.getString("End Body"));

                List<Vector2> path = PhysicsUtilities.parsePoints(components.getJSONObject("Path").getString("Points"), false, 0, 0);
                entity.add(new Rope(path, Rope.STANDARD_SEGMENT_THICKNESS, Rope.STANDARD_SEGMENT_LENGTH,
                        startBody, endBody));

                entity.get(Rope.class).segmentSprite = new Sprite(mGame.textureManager.get("rope"), 0);
                return true;
            }
            else if (entityName.startsWith("hanger")) {
                PolygonShape hangerShape = new PolygonShape();
                hangerShape.setAsBox(16* PhysicsSystem.METERS_PER_PIXEL, 8*PhysicsSystem.METERS_PER_PIXEL);
                transformation = components.getJSONObject("Transformation");
                entity.add(new Transformation((float) transformation.getDouble("Translate-X"),
                        (float) transformation.getDouble("Translate-Y"), (float) transformation.getDouble("Angle")));
                entity.add(new PhysicsBody(world, BodyDef.BodyType.StaticBody, entity, hangerShape, new PhysicsBody.Properties(true)));;
                entity.add(new Hanger());
                //entity.add(new Sprite(mGame.textureManager.get("hanger"), -1));
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addComponent(com.badlogic.gdx.physics.box2d.World world, Entity entity, String compName, JSONObject component, JSONObject components) {
        try {
            if (compName.equals("Explosive")) {
                entity.add(new ExplosionSystem.Explosive());
            }
            else if (compName.equals("Mover")) {
                Mover m = new Mover();
                m.type = Mover.Type.LINEAR;
                m.finalX = (float)component.getDouble("Final-X");
                m.finalY = (float)component.getDouble("Final-Y");
                entity.add(m);
            }
            else if (compName.equals("Rotor")) {
                Mover m = new Mover();
                m.type = Mover.Type.ANGULAR;
                m.finalAngle = (float)component.getDouble("Final-Angle");
                entity.add(m);
            }
            else if (compName.equals("Lever")) {
                final Trigger trigger = new Trigger();
                trigger.tag = component.getString("Tag");
                entity.add(trigger);

                String mentity = trigger.tag.substring(0, trigger.tag.indexOf("_lever"));
                trigger.object = levelLoader.mCurrentEntities.get(mentity);
                entity.get(Trigger.class).listeners.add(new Trigger.Listener() {
                    @Override
                    public void onTriggered(boolean status) {
                        Entity moverEntity = (Entity) trigger.object;
                        Mover m = moverEntity.get(Mover.class);
                        m.start(moverEntity.get(Transformation.class));
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onWorldInitialized(World world) {

    }
}
