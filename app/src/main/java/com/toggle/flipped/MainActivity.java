package com.toggle.flipped;

import android.app.Activity;
import android.os.Bundle;

import com.toggle.katana2d.Engine;

public class MainActivity extends Activity {

    private Engine mEngine = new Engine();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEngine.init(this);
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
}