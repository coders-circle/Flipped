package com.toggle.katana2d;

import android.app.Activity;
import android.os.Bundle;

public abstract class GameActivity  extends Activity {
    protected Engine mEngine = new Engine();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEngine.init(this);

        init();
    }

    protected void init() {
        start();
    }

    protected void start() {
        mEngine.start();
    }

    public void onGamePreStart() {

    }

    @Override
    public void onPause() {
        super.onPause();
        mEngine.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mEngine.onResume();
    }

    public void onGameStart() {

    }
}
