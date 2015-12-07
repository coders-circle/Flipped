package com.toggle.flipped;

import android.graphics.Typeface;
import android.util.Log;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Font;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.TouchInputData;

import java.util.List;

public class MenuScreen extends Scene {

    private Listener mListener;

    public static interface Listener {
        void onPlay();
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

        font = new Font(mGame.getRenderer(), Typeface.SANS_SERIF, 20.0f );
        systemMenu = new Menu();
        systemMenu.setup(font);

        TouchInputData input = mGame.getTouchInputData();
        input.tap.x = -1;
        input.tap.y = -1;

        Entity backgroundMusic = new Entity();
        Sound s = new Sound();
        s.addSource(mGame.getActivity(), R.raw.sound_menubg, Sound.AMBIANCE);
        //s.state = Sound.AMBIANCE;
        backgroundMusic.add(s);

        addEntity(backgroundMusic);
    }

    @Override
    public void onDraw(){
        systemMenu.draw();
    }

    @Override
    public void onUpdate(float dt){
        TouchInputData input = mGame.getTouchInputData();
        TouchInputData.Pointer p = input.tap;
        //for (int i=0; i<input.pointers.size(); ++i) {
            //TouchInputData.Pointer p = input.pointers.valueAt(i);
            //if (input.pointers.size() > 0) {
                switch (systemMenu.hitTest(p.x, p.y)) {
                    case 0:
                        mListener.onPlay();
                        Log.v("systemMenu", "start game");
                        break;
                    case 1:
                        // Settings
                        Log.v("systemMenu", "settings");
                        break;
                    case 2:
                        Log.v("systemMenu", "exit");
                        // exit
                        break;
                }

            //}
        //}
        input.tap.x = -1;
        input.tap.y = -1;
    }


}
