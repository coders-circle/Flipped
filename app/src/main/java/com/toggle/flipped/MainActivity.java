package com.toggle.flipped;

import android.opengl.GLES20;
import android.view.KeyEvent;

import com.toggle.katana2d.Background;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.GameActivity;

public class MainActivity extends GameActivity implements Level.Listener, MenuScreen.Listener/* implements SplashScreen.SplashScreenListener*/ {
    //private List<Level> levels = new ArrayList<>();

    /*private SplashScreen splashScreen = new SplashScreen(this);
    private MenuScreen menuScreen = new MenuScreen(levels);*/

    Level level1, level2, mActiveLevel;
    MenuScreen menu;
    SplashScreen splashScreen;

    @Override
    public void onGameStart() {
        final Game game = mEngine.getGame();

        level1 = new Level1(game, this);
        level2 = new Level2(game, this);

        splashScreen = new SplashScreen();
        game.addScene(splashScreen);

        menu = new MenuScreen(this);
        game.addScene(menu);

        splashScreen.data.background = game.getRenderer().addTexture(R.drawable.splash, game.getRenderer().width, game.getRenderer().height);
        splashScreen.data.background.originX = 0;
        splashScreen.data.background.originY = 0;
        splashScreen.data.showTime = 2.4f;
        game.setActiveScene(splashScreen.sceneId);

        splashScreen.listener = new SplashScreen.Listener() {
            @Override
            public void onShown() {
                game.setActiveScene(menu.sceneId);
            }
        };

        /*// add splash-screen scene
        game.setActiveScene(game.addScene(splashScreen));

        // add levels
        levels.add(new TestLevel(game));

        // add menu-screen scene
        game.addScene(menuScreen);*/

        loadResources();
        //game.setActiveScene(game.addScene(new TestScene2()));
        // level1.load();
    }

    public void loadResources() {
        Game game = mEngine.getGame();

        // Rope sprite
        game.textureManager.add("rope", game.getRenderer().addTexture(/*new float[]{1, 0.5f, 0, 1}*/R.drawable.rope_segment,
                Rope.STANDARD_SEGMENT_LENGTH, Rope.STANDARD_SEGMENT_THICKNESS));

        // Snow sprite
        game.textureManager.add("snow", game.getRenderer().addTexture(R.drawable.snow_flake, 32, 32));
    }

    @Override
    public void onLevelComplete(Level level) {
        level.unload();
        if (level == level1) {
            level2.load();
            mActiveLevel = level2;
        }

    }

    @Override
    public void onLevelPaused(Level level) {
        mPause = true;
        mPausedLevel = level;
        Game game = mEngine.getGame();
        game.setActiveScene(menu.sceneId);
        mActiveLevel = null;
    }

    @Override
    public SplashScreen getSplashScreen() {
        return splashScreen;
    }

    private boolean mPause = false;
    private Level mPausedLevel = null;

    @Override
    public void onPlay() {
        if (mPause && mPausedLevel != null) {
            mPausedLevel.resumeLevel();
            mActiveLevel = mPausedLevel;
        } else {
            level1.load();
            mActiveLevel = level1;
        }
    }

    @Override
    public void onExit() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_BACK:
                if (mActiveLevel != null) {
                    mActiveLevel.pauseLevel();
                    return true;
                }
                else if (mEngine.getGame().getActiveScene() == splashScreen)
                    return true;
        }
        return super.onKeyDown(keycode, e);
    }

    /*@Override
    public void onFinish() {
        Game game = mEngine.getGame();

        // Load resources
        loadResources();

        // change to menu scene
        game.setActiveScene(menuScreen.sceneId);
    }*/
}
