package com.toggle.flipped;

import android.util.Log;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.GameActivity;

public class MainActivity extends GameActivity implements Level.Listener/* implements SplashScreen.SplashScreenListener*/ {
    //private List<Level> levels = new ArrayList<>();

    /*private SplashScreen splashScreen = new SplashScreen(this);
    private MenuScreen menuScreen = new MenuScreen(levels);*/

    Level level1, level2;

    @Override
    public void onGameStart() {
        Game game = mEngine.getGame();

        /*// add splash-screen scene
        game.setActiveScene(game.addScene(splashScreen));

        // add levels
        levels.add(new TestLevel(game));

        // add menu-screen scene
        game.addScene(menuScreen);*/

        loadResources();
        //game.setActiveScene(game.addScene(new TestScene2()));

        level1 = new Level1(game, this);
        level2 = new Level2(game, this);
        level1.load();
    }

    public void loadResources() {
        Game game = mEngine.getGame();

        // Rope sprite
        game.textureManager.add("rope", game.getRenderer().addTexture(new float[]{1, 0.5f, 0, 1},
                Rope.STANDARD_SEGMENT_LENGTH, Rope.STANDARD_SEGMENT_THICKNESS));
    }

    @Override
    public void onLevelComplete(Level level) {
        level.unload();
        if (level == level1)
            level2.load();

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
