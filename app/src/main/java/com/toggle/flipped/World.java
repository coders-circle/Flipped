package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.Background;
import com.toggle.katana2d.BackgroundSystem;
import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.ParticleSystem;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
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

        // Flipped's systems, controlling inputs, player, mirror, wind, rope etc.
        mSystems.add(new BotControlSystem());
        mSystems.add(new PlayerInputSystem(mGame));
        mSystems.add(flipSystem);
        mSystems.add(new WindSystem());
        mSystems.add(new RopeSystem(physicsSystem.getWorld(), mGame.getRenderer()));

        // Load the entities from the level editor
        mLevelLoader.loadWorld(mWorldName, this, physicsSystem.getWorld());

        mPlayer = mParentLevel.levelLoader.mEntities.get("player");
        flipSystem.setPlayer(mPlayer);

        startX = mPlayer.get(PhysicsBody.class).body.getPosition().x;
        startY = mPlayer.get(PhysicsBody.class).body.getPosition().y;

        Entity bk1 = new Entity();
        bk1.add(new Background(mGame.getRenderer().addTexture(R.drawable.main_background, 640, 400), 100));
        addEntity(bk1);

        Entity bk2 = new Entity();
        bk2.add(new Background(mGame.getRenderer().addTexture(R.drawable.hills, 640, 480), 50));
        addEntity(bk2);

        Entity bk3 = new Entity();
        bk3.add(new Background(mGame.getRenderer().addTexture(R.drawable.trees, 640, 480), 30));
        addEntity(bk3);

        Entity bk4 = new Entity();
        bk4.add(new Background(mGame.getRenderer().addTexture(R.drawable.pa1th, 7520, 640), 0));
        addEntity(bk4);
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
