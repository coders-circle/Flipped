package com.toggle.flipped;

import android.graphics.Typeface;

import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Font;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.TouchInputData;

import java.util.List;

public class MenuScreen extends Scene {

    private Listener mListener;

    public interface Listener {
        void onPlay();
        void onExit();
    }

    private List<Level> mLevels;
    Font font;
    Menu systemMenu;

    public MenuScreen(Listener listener){
        mListener = listener;
    }

    @Override
    public void onInit() {
        mSystems.add(new RenderSystem(mGame.getRenderer()));
        mSystems.add(new SoundSystem());
        font = new Font(mGame.getRenderer(), Typeface.createFromAsset(mGame.getActivity().getAssets(), "IndieFlower.ttf"), 20.0f);
        systemMenu = new Menu();
        systemMenu.setup(font);

        TouchInputData input = mGame.getTouchInputData();
        input.tap.x = -1;
        input.tap.y = -1;

        Entity backgroundMusic = new Entity();
        Sound s = new Sound();
        s.addSource(mGame.getActivity(), R.raw.sound_menubg, Sound.AMBIANCE);
        s.soundSources.get(s.soundSources.size()-1).setLooping(true);
        s.state = Sound.AMBIANCE;
        backgroundMusic.add(s);

        addEntity(backgroundMusic);
    }

    @Override
    public void onDraw(){
        Camera c = mGame.getRenderer().getCamera();
        c.x = c.y = 0;
        systemMenu.draw();
    }

    @Override
    public void onUpdate(float dt){
        TouchInputData input = mGame.getTouchInputData();
        TouchInputData.Pointer p = input.tap;
        switch (systemMenu.hitTest(p.x, p.y)) {
            case 0:
                mListener.onPlay();
                break;
            case 1:
                // Settings
                break;
            case 2:
                mListener.onExit();
                // exit
                break;
        }
        input.tap.x = -1;
        input.tap.y = -1;
    }
}
