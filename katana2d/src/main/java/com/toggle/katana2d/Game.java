package com.toggle.katana2d;


import java.util.ArrayList;
import java.util.List;

public class Game implements TimerCallback {
    // Some common resources
    // Textures
    public Manager<Texture> textureManager = new Manager<>();

    // A reference to the renderer
    private final GLRenderer mRenderer;
    // Timer with 60 FPS as target
    private Timer mTimer = new Timer(60f);

    // The activity that runs this game
    private GameActivity mActivity;

    public Game(GameActivity activity) {
        mRenderer = new GLRenderer(activity, this);
        mActivity = activity;
    }

    public GameActivity getActivity() { return mActivity; }

    public GLRenderer getRenderer() { return mRenderer; }

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
        mActiveScene.onActiveStateChanged(true);
    }

    // Get a scene from index
    public Scene getScene(int index) {
        return mScenes.get(index);
    }


    private boolean mStop = false;
    private final Integer drawLock = 1;

    // called on surface creation
    public void init() {
        if (!mInitialized) {
            mActivity.onGamePreStart();
            for (Scene scene : mScenes)
                scene.init(this);
            mInitialized = true;
            mActivity.onGameStart();

            // start updating in separate thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!mStop) {
                        // Update using timer
                        mDrawInterpolation = mTimer.update(Game.this);

                        // After each update, we need to draw, notify the render thread to wakeup if sleeping
                        synchronized (drawLock) {
                            drawLock.notify();
                        }

                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

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
        // sleep till the update thread wakes us up
        synchronized (drawLock) {
            try {
                drawLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // draw a frame
        if (mActiveScene != null)
            mActiveScene.draw(mDrawInterpolation);
    }

    private float mDrawInterpolation;

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

