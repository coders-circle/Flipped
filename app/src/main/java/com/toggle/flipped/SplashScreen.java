package com.toggle.flipped;

import android.graphics.Typeface;
import android.util.Log;

import com.toggle.katana2d.Background;
import com.toggle.katana2d.BackgroundSystem;
import com.toggle.katana2d.Boundary;
import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Font;
import com.toggle.katana2d.GLRenderer;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.SimpleTexture;
import com.toggle.katana2d.Texture;
import com.toggle.katana2d.Timer;

public class SplashScreen extends Scene {
    public Listener listener;

    public interface Listener {
        void onShown();
    }

    private Font font;
    public SplashScreenData data = new SplashScreenData();

    public static class SplashScreenData {
        public String text;
        public Texture background;
        public float showTime = -1;
    }

    private int shown = 0;

    @Override
    public void onInit() {
        font = new Font(mGame.getRenderer(), Typeface.create(Typeface.createFromAsset(mGame.getActivity().getAssets(), "IndieFlower.ttf"), Typeface.BOLD), 20.0f);
    }

    private float mShownTime = 0;

    @Override
    public void onUpdate(float deltaTime) {
        float currentTime = System.nanoTime();
        float dt = (currentTime - mLastTime)/ Timer.ONE_SECOND;
        mLastTime = currentTime;

        mShownTime += dt;
        if (data.showTime > 0 && mShownTime >= data.showTime) {
            listener.onShown();
        }
    }

    @Override
    public void onDraw(){
        GLRenderer renderer = mGame.getRenderer();
        Camera c = renderer.getCamera();
        c.x = c.y = 0; c.angle = 0;

        if (data.background != null) {
            data.background.draw(renderer, 0, 0, -1, 0, 1, 1);
        }
        if (data.text != null) {
            Boundary b = font.calculateBoundary(data.text, 0, 0, 0, 1, 1);
            float x = renderer.width/2 - (b.right-b.left)/2;
            float y = renderer.height/2 - (b.bottom-b.top)/2;

            font.draw(data.text, x, y, 0, 1, 1);
        }

        if (shown > 10) {
            if (data.showTime < 0 && listener != null)
                listener.onShown();
        }

        shown ++;
    }

    private float mLastTime = 0;
    @Override
    public void onActiveStateChanged(boolean active) {
        if (active) {
            mGame.getRenderer().enablePostProcessing = false;
            shown = 0;
            mShownTime = 0;
            mLastTime = System.nanoTime();
            Camera c = mGame.getRenderer().getCamera();
            c.x = c.y = 0; c.angle = 0;
        }
    }
}
