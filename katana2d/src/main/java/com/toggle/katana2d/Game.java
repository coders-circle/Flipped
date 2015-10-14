package com.toggle.katana2d;


import android.content.Context;
import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

public class Game  implements TimerCallback {
    // A reference to the renderer
    private final GLRenderer mRenderer;
    // Timer with 60 FPS as target
    private Timer mTimer = new Timer(60.0);

    public Game(Context context) {
        mRenderer = new GLRenderer(context, this);
    }

    public GLRenderer getRenderer() { return mRenderer; }

    // List of all scenes
    private List<Scene> mScenes = new ArrayList<>();
    // Active scene
    private Scene mActiveScene;

    // Already initialized?
    private boolean mInitialized = false;

    // Add a new scene and get its index
    public int addScene(Scene scene) {
        mScenes.add(scene);
        if (mInitialized)
            scene.init(this);

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
        for (Scene scene : mScenes)
            scene.init(this);
        mInitialized = true;
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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (mActiveScene != null)
            mActiveScene.draw();
    }

    /*TODO: Get Touch input data and generate input events for the active scene.*/
}

