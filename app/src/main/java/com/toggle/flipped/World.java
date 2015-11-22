package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.ParticleSystem;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

//import org.jbox2d.common.Vec2;

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
        mSystems.add(new RenderSystem(mGame.getRenderer()));
        mSystems.add(physicsSystem);
        mSystems.add(particleSystem);

        mSystems.add(new BotControlSystem());
        mSystems.add(new PlayerInputSystem(mGame));
        //mSystems.add(flipSystem);
        mSystems.add(new WindSystem());
        mSystems.add(new RopeSystem(physicsSystem.getWorld(), mGame.getRenderer()));

        // Load the entities from the level editor
        mLevelLoader.loadWorld(mWorldName, this, physicsSystem.getWorld());

        mPlayer = mParentLevel.levelLoader.mEntities.get("player");
        flipSystem.setPlayer(mPlayer);

        startX = mPlayer.get(PhysicsBody.class).body.getPosition().x;
        startY = mPlayer.get(PhysicsBody.class).body.getPosition().y;
    }

    private float startX, startY;

    // When world changes from another world to this
    public void load() {
        Camera camera = mGame.getRenderer().getCamera();
        camera.angle = mAngle;

        mPlayer.get(PhysicsBody.class).body.setTransform(new Vector2(startX, startY), 0);
    }

    // When world changes from this to another world
    public void unload() {

    }
}
