package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.BackgroundSystem;
import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.ParticleSystem;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsBody;
import com.toggle.katana2d.physics.PhysicsSystem;

// On world is one scene, one level contains multiple worlds
public class World extends Scene {

    public interface WorldEventListener {
        void onWorldInitialized(World world);
    }

    private LevelLoader mLevelLoader;
    private String mWorldName;
    private Level mParentLevel;
    private FlipSystem mFlipSystem;

    public int mSceneId;    // Id corresponding to Game's scenes list
    public float mAngle;   // Angle of the world: 180 for flip, 0 for normal

    private Entity mPlayer; // Player entity; every world has one
    public com.badlogic.gdx.physics.box2d.World physicsWorld;

    private float mWidth, mHeight;

    public float getWidth() { return mWidth; }
    public float getHeight() { return mHeight; }

    public World(Level parentLevel, LevelLoader levelLoader, String worldName, float width, float height) {
        mLevelLoader = levelLoader;
        mWorldName = worldName;
        mParentLevel = parentLevel;
        mAngle = 0;
        mWidth = width; mHeight = height;
    }

    @Override
    public void onInit() {
        PhysicsSystem physicsSystem = new PhysicsSystem();
        mFlipSystem = new FlipSystem(mParentLevel);
        ParticleSystem particleSystem = new ParticleSystem();
        physicsWorld = physicsSystem.getWorld();

        // Add the systems

        // The standard systems provided by Katana-2D
        mSystems.add(new BackgroundSystem(mGame.getRenderer()));
        mSystems.add(new RenderSystem(mGame.getRenderer()));
        mSystems.add(physicsSystem);
        mSystems.add(particleSystem);

        // Flipped's systems, controlling inputs, player, mirror, wind, rope, explosives etc.
        mSystems.add(new BotControlSystem());
        mSystems.add(new PlayerInputSystem(mGame, mWidth, mHeight));
        mSystems.add(new WindSystem());
        mSystems.add(new RopeSystem(physicsWorld, mGame.getRenderer()));
        mSystems.add(new ExplosionSystem(physicsWorld));
        mSystems.add(new SoundSystem());
        mSystems.add(new PickCarrySystem());
        mSystems.add(new BurnSystem());
        mSystems.add(new TriggerSystem());
        mSystems.add(new MoverSystem());
        mSystems.add(mFlipSystem);

        if (mParentLevel.getClass() == Level1.class && mWorldName.equals("world1"))
            mSystems.add(new HelpSystem(mGame.getRenderer()));

        // Load the entities from the level editor
        mLevelLoader.loadWorld(mWorldName, this, physicsWorld);

        mPlayer = mParentLevel.levelLoader.mCurrentEntities.get("player");
        mFlipSystem.setPlayer(mPlayer);

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

        //mPlayer.get(Sprite.class).mixColor = new float[] {1,0.3f,0.3f,1};

        mFlipSystem.incoming = true;

        float x, y;
        if (entryMirror == null) {
            x = startX; y = startY;
        } else {
            x = entryMirror.get(Transformation.class).x;
            y = entryMirror.get(Transformation.class).y - 32;
            x *= PhysicsSystem.METERS_PER_PIXEL; y *= PhysicsSystem.METERS_PER_PIXEL;
        }

        mPlayer.get(PhysicsBody.class).body.setTransform(new Vector2(x, y), 0);
        mGame.getRenderer().centerCamera(x, y, mWidth, mHeight);
    }

    @Override
    public void onActiveStateChanged(boolean active) {
        if (active) {
            mGame.getRenderer().enablePostProcessing = false;

            float x = mPlayer.get(Transformation.class).x;
            float y = mPlayer.get(Transformation.class).y;
            mGame.getRenderer().centerCamera(x, y, mWidth, mHeight);
        }
    }

    @Override
    public void onUpdate(float dt) {
        if (mPlayer.get(Bot.class).dead)
            mParentLevel.restart();
        //Log.d("dead", mPlayer.get(Bot.class).dead+"");
    }

    // When world changes from this to another world
    public void unload() {

    }

    @Override
    public void onPause() {
        mParentLevel.pauseLevel();
    }
}
