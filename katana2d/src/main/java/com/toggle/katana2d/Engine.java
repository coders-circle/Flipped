package com.toggle.katana2d;

import android.app.Activity;

public class Engine {

    private SurfaceView mSurfaceView;
    private Game mGame;
    private Activity mActivity;

    public void init(Activity activity)
    {
        mGame = new Game(activity);
        mActivity = activity;
    }

    public void start() {
        mSurfaceView = new SurfaceView(mActivity, mGame.getRenderer());
        mActivity.setContentView(mSurfaceView);
    }

    public Game getGame() { return mGame; }

    public void onPause()
    {
        mSurfaceView.onPause();
    }

    public void onResume()
    {
        mSurfaceView.onResume();
    }
}
