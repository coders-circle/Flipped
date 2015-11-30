package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.BackgroundSystem;
import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.ParticleSystem;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

// On world is one scene, one level contains multiple worlds
public class World extends Scene {

    private LevelLoader mLevelLoader;
    private String mWorldName;
    private Level mParentLevel;

    public int mSceneId;    // Id corresponding to Game's scenes list
    public float mAngle;   // Angle of the world: 180 for flip, 0 for normal

    private Entity mPlayer; // Player entity; every world has one

    public World(Level parentLevel, LevelLoader levelLoader, String worldName) {
        mLevelLoader = levelLoader;
        mWorldName = worldName;
        mParentLevel = parentLevel;
        mAngle = 0;
    }

    @Override
    public void onInit() {
        PhysicsSystem physicsSystem = new PhysicsSystem();
        FlipSystem flipSystem = new FlipSystem(mParentLevel);
        ParticleSystem particleSystem = new ParticleSystem();

        // Add the systems

        // The standard systems provided by Katana-2D
        mSystems.add(new BackgroundSystem(mGame.getRenderer()));
        mSystems.add(new RenderSystem(mGame.getRenderer()));
        mSystems.add(physicsSystem);
        mSystems.add(particleSystem);

        // Flipped's systems, controlling inputs, player, mirror, wind, rope, explosives etc.
        mSystems.add(new BotControlSystem());
        mSystems.add(new PlayerInputSystem(mGame));
        mSystems.add(flipSystem);
        mSystems.add(new WindSystem());
        mSystems.add(new RopeSystem(physicsSystem.getWorld(), mGame.getRenderer()));
        mSystems.add(new ExplosionSystem(physicsSystem.getWorld(), mGame));
        mSystems.add(new SoundSystem());

        // Load the entities from the level editor
        mLevelLoader.loadWorld(mWorldName, this, physicsSystem.getWorld());

        mPlayer = mParentLevel.levelLoader.mCurrentEntities.get("player");
        flipSystem.setPlayer(mPlayer);

        startX = mPlayer.get(PhysicsBody.class).body.getPosition().x;
        startY = mPlayer.get(PhysicsBody.class).body.getPosition().y;

        mParentLevel.onWorldInitialized(this);

        mLevelLoader.mCurrentEntities.clear();
        mLevelLoader.mCurrentScene = null;
    }

    private float startX, startY;

    // When world changes from another world to this
    public void load(Entity entryMirror) {
        Camera camera = mGame.getRenderer().getCamera();
        camera.angle = mAngle;

        float x, y;
        if (entryMirror == null) {
            x = startX; y = startY;
        } else {
            x = entryMirror.get(Transformation.class).x;
            y = entryMirror.get(Transformation.class).y - 32;
        }

        mPlayer.get(PhysicsBody.class).body.setTransform(new Vector2(x, y), 0);
    }

    // When world changes from this to another world
    public void unload() {

    }

    public interface WorldEventListener {
        void onWorldInitialized(World world);
    }
}
