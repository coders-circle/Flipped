package com.toggle.flipped;

import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;

public class SplashScreen extends Scene {

    public interface SplashScreenListener {
        void onFinish();
    }

    private SplashScreenListener mListener;

    public SplashScreen(SplashScreenListener listener) {
        mListener = listener;
    }

    @Override
    public void onInit() {
        mSystems.add(new RenderSystem(mGame.getRenderer()));

        mListener.onFinish();
    }
}
