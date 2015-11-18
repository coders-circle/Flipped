package com.toggle.flipped;

import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.ParticleSystem;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.physics.PhysicsSystem;

// On world is one scene, one level contains multiple worlds
public class World extends Scene {

    private LevelLoader mLevelLoader;
    private String mWorldName;
    public int mSceneId;    // Id corresponding to Game's scenes list
    private Level mParentLevel;
    private float mAngle;   // Angle of the world: 180 for flip, 0 for normal
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
        mSystems.add(new RenderSystem(mGame.getRenderer()));
        mSystems.add(physicsSystem);
        mSystems.add(particleSystem);

        mSystems.add(new BotControlSystem());
        mSystems.add(new PlayerInputSystem(mGame));
        mSystems.add(flipSystem);
        mSystems.add(new WindSystem());
        mSystems.add(new RopeSystem(physicsSystem.getWorld(), mGame.getRenderer()));

        // Load the entities from the level editor
        mLevelLoader.loadWorld(mWorldName, this, physicsSystem.getWorld());

        mPlayer = mParentLevel.standardEntities.get("player");
        flipSystem.setPlayer(mPlayer);
    }

    // When world changes from another world to this
    public void load() {
        Camera camera = mGame.getRenderer().getCamera();
        camera.angle = mAngle;
    }

    // When world changes from this to another world
    public void unload() {

    }
}
