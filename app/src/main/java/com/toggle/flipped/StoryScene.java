package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.Background;
import com.toggle.katana2d.BackgroundSystem;
import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.MultiTexture;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.Timer;
import com.toggle.katana2d.TouchInputData;

public class StoryScene extends Scene {
    private Listener mListener;

    public interface Listener {
        void onExit();
    }

    public StoryScene(Listener listener){
        mListener = listener;
    }

    @Override
    public void onInit() {
        mSystems.add(new BackgroundSystem(mGame.getRenderer()));
        mSystems.add(new RenderSystem(mGame.getRenderer()));
        mSystems.add(new SoundSystem());

        TouchInputData input = mGame.getTouchInputData();
        input.tap.x = -1;
        input.tap.y = -1;

        /*Entity backgroundMusic = new Entity();
        Sound s = new Sound();
        s.addSource(mGame.getActivity(), R.raw.sound_menubg, Sound.AMBIANCE);
        s.soundSources.get(s.soundSources.size()-1).setLooping(true);
        s.state = Sound.AMBIANCE;
        backgroundMusic.add(s);

        addEntity(backgroundMusic);*/

        backTexture = mGame.getRenderer().addTexture(R.drawable.story, 512, 640, 2, 1);
        Entity back = new Entity();
        back.add(new Background(backTexture, 1));
        addEntity(back);

        backTexture.color = new float[] {1,1,1,0};
    }

    private MultiTexture backTexture;

    private Vector2[] positions = {
            new Vector2(0, 0), new Vector2(1024*1.1f/2, 0), new Vector2(1024, 0),
            new Vector2(1024, 600), new Vector2(512, 600), new Vector2(0, 600),
    };

    private final static int MAX = 6;
    private int state = 0;  // 0 for fade-in, MAX+1 for fade-out
    private boolean moving = false;
    private float time;
    private static final float HOVER_TIME = 1.5f;
    private static final float MOVE_TIME = 1;
    private float lastTime = 0;

    @Override
    public void onUpdate(float deltaTime) {

        float currentTime = System.nanoTime();
        float dt = (currentTime - lastTime) / Timer.ONE_SECOND;
        lastTime = currentTime;

        Camera cam = mGame.getRenderer().getCamera();
        cam.angle = 0;

        mGame.getTimer().getTotalTime();
        time += dt;

        float alpha = Math.min(1, time/HOVER_TIME);
        if (moving)
            alpha = Math.min(1, time/MOVE_TIME);
        float minusalpha = 1 - alpha;

        if (state == 0) {
            cam.x = positions[0].x;
            cam.y = positions[0].y;
            backTexture.color[3] = alpha;
        } else if (state >= MAX+1) {
            backTexture.color[3] = minusalpha;
        } else {
            backTexture.color[3] = 1;
            cam.x = positions[state - 1].x;
            cam.y = positions[state - 1].y;
        }

        if (moving && state < MAX) {
            float nextX = positions[state].x;
            float nextY = positions[state].y;
            cam.x = nextX * alpha + cam.x * minusalpha;
            cam.y = nextY * alpha + cam.y * minusalpha;
        }

        if (state <= MAX)
            mGame.getRenderer().centerCamera(cam.x, cam.y, 1024, 640);


        if ((!moving && time>HOVER_TIME) || (moving && time>MOVE_TIME)) {
            if (state == MAX+1)
                mListener.onExit();

            if (!moving && state > 0 && state < MAX)
                moving = true;
            else {
                moving = false;
                state++;
            }
            time = 0;
        }

        TouchInputData input = mGame.getTouchInputData();
        if (input.pointers.size()>0 && state < MAX+1) {
            if (state > 0)
                time = 0;
            state = MAX+1;
        }
    }

    @Override
    public void onActiveStateChanged(boolean active) {
        if (active) {
            mGame.getRenderer().enablePostProcessing = false;
            lastTime = System.nanoTime();
        }
    }
}
