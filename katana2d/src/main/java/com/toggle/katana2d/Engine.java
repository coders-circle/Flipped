package com.toggle.katana2d;

import android.app.Activity;

public class Engine {

    private SurfaceView mSurfaceView;

    public void init(Activity context)
    {
        Game game = new Game(context);
        mSurfaceView = new SurfaceView(context, game.getRenderer());
        context.setContentView(mSurfaceView);
    }

    public void onPause()
    {
        mSurfaceView.onPause();
    }

    public void onResume()
    {
        mSurfaceView.onResume();
    }
}
