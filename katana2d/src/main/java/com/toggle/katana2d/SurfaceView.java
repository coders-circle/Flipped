package com.toggle.katana2d;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class SurfaceView extends GLSurfaceView {

    public SurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        Game game = new Game(context);
        setRenderer(game.getRenderer());
    }

    public SurfaceView(Context context, Renderer renderer){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
    }

    /*TODO: Get Touch input data and send it to the Game class.*/
}