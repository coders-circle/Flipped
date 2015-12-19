package com.toggle.flipped;

import android.graphics.Typeface;

import com.toggle.katana2d.Background;
import com.toggle.katana2d.BackgroundSystem;
import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Font;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Sprite;
import com.toggle.katana2d.TouchInputData;
import com.toggle.katana2d.Transformation;

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
        mSystems.add(new BackgroundSystem(mGame.getRenderer()));
        mSystems.add(new RenderSystem(mGame.getRenderer()));
        mSystems.add(new SoundSystem());
        font = new Font(mGame.getRenderer(), Typeface.create(Typeface.createFromAsset(mGame.getActivity().getAssets(), "IndieFlower.ttf"), Typeface.BOLD), 20.0f);
        systemMenu = new Menu();
        systemMenu.setup(font);

        Entity boy = new Entity();
        boy.add(new Sprite(mGame.getRenderer().addTexture(R.drawable.walk, 24*60f/48f, 60), 0, 5, 2));
        boy.add(new Transformation(100, mGame.getRenderer().height - 100, -15));
        addEntity(boy);

        Entity dog = new Entity();
        dog.add(new Sprite(mGame.getRenderer().addTexture(R.drawable.dog_run, 120, 60), 0, 3, 3));
        dog.add(new Transformation(mGame.getRenderer().width - 100, 100, -15));
        dog.get(Sprite.class).scaleX = -1;
        addEntity(dog);

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

        Entity back = new Entity();
        back.add(new Background(mGame.getRenderer().addTexture(R.drawable.menu_back, mGame.getRenderer().width, mGame.getRenderer().height), 1));
        addEntity(back);
    }

    @Override
    public void onDraw(){
        Camera c = mGame.getRenderer().getCamera();
        c.x = c.y = 0; c.angle = 0;
        systemMenu.draw();
    }

    @Override
    public void onUpdate(float dt){
        TouchInputData input = mGame.getTouchInputData();
        TouchInputData.Pointer p = input.tap;
        switch (systemMenu.hitTest(p.x, p.y)) {
            case 0:
                // Start game
                mListener.onPlay();
                break;
            case 1:
                // Settings
                break;
            case 2:
                // exit
                mListener.onExit();
                break;
        }
        input.tap.x = -1;
        input.tap.y = -1;
    }

    @Override
    public void onActiveStateChanged(boolean active) {
        if (active)
            mGame.getRenderer().enablePostProcessing = false;
    }
}
