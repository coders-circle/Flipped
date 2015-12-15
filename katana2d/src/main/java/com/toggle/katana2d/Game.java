package com.toggle.katana2d;


import java.util.ArrayList;
import java.util.List;

public class Game implements TimerCallback {
    // Some common resources
    // Textures
    public Manager<Texture> textureManager = new Manager<>();

    // A reference to the renderer
    private final GLRenderer mRenderer;
    // Timer with fixed target FPS
    private Timer mTimer = new Timer(30f);

    // The activity that runs this game
    private GameActivity mActivity;

    public Game(GameActivity activity) {
        mRenderer = new GLRenderer(activity, this);
        mActivity = activity;
    }

    public GameActivity getActivity() { return mActivity; }

    public GLRenderer getRenderer() { return mRenderer; }

    public Timer getTimer() { return mTimer; }

    public TouchInputData getTouchInputData() { return mRenderer.touchInputData; }

    // List of all scenes
    private List<Scene> mScenes = new ArrayList<>();
    // Active scene
    private Scene mActiveScene;

    // Already initialized?
    private boolean mInitialized = false;

    // Add a new scene and get its index
    public int addScene(Scene scene) {
        mScenes.add(scene);
        if (mInitialized) {
            scene.init(this);
        }

        int id = mScenes.size()-1;
        scene.sceneId = id;
        return id;
    }

    // Set active scene
    public void setActiveScene(int index) {
        if (mActiveScene != null)
            mActiveScene.onActiveStateChanged(false);
        mActiveScene = getScene(index);
        if (mActiveScene != null)
            mActiveScene.onActiveStateChanged(true);
    }

    // Get a scene from index
    public Scene getScene(int index) {
        try {
            return mScenes.get(index);
        }
        catch (Exception ex) {
            return null;
        }
    }

    // called on surface creation
    public void init() {
        if (!mInitialized) {
            mActivity.onGamePreStart();
            for (Scene scene : mScenes)
                scene.init(this);
            mInitialized = true;
            mActivity.onGameStart();
        }
    }

    // update method for updating game logic and animations, which are time dependent
    @Override
    public void update(float deltaTime) {
        if (mActiveScene != null)
            mActiveScene.update(deltaTime);
    }

    // draw method for rendering stuffs
    public void newFrame() {
        float drawInterpolation = mTimer.update(this);
        // draw a frame
        if (mActiveScene != null)
            mActiveScene.draw(drawInterpolation);
    }

    // pause and resume events
    public void onPause() {
        for (Scene scene: mScenes)
            scene.onPause();
    }

    public void onResume() {
        for (Scene scene: mScenes)
            scene.onResume();
    }
}

