package com.toggle.katana2d;


import java.util.ArrayList;
import java.util.List;

public class Game implements TimerCallback {
    // Some common resources
    // Sprites
    public Manager<GLSprite> spriteManager = new Manager<>();

    // A reference to the renderer
    private final GLRenderer mRenderer;
    // Timer with 40 FPS as target
    private Timer mTimer = new Timer(40.0);

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

        return mScenes.size()-1;
    }

    // Set active scene
    public void setActiveScene(int index) {
        mActiveScene = getScene(index);
    }

    // Get a scene from index
    public Scene getScene(int index) {
        return mScenes.get(index);
    }

    // called on surface creation
    public void init() {
        if (!mInitialized) {
            mActivity.onGameStart();
            for (Scene scene : mScenes)
                scene.init(this);
            mInitialized = true;
        }
    }

    // called on each frame
    public void newFrame() {
        mTimer.Update(this);
        draw();
    }

    // update method for updating game logic and animations, which are time dependent
    @Override
    public void update(double deltaTime) {
        if (mActiveScene != null)
            mActiveScene.update(deltaTime);
    }

    // draw method for all rendering operations
    public void draw() {
        if (mActiveScene != null)
            mActiveScene.draw();
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

