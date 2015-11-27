package com.toggle.flipped;

import android.graphics.PixelFormat;
import android.os.Bundle;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.GameActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GameActivity/* implements SplashScreen.SplashScreenListener*/ {
    //private List<Level> levels = new ArrayList<>();

    /*private SplashScreen splashScreen = new SplashScreen(this);
    private MenuScreen menuScreen = new MenuScreen(levels);*/

    @Override
    public void onGameStart() {
        Game game = mEngine.getGame();

        /*// add splash-screen scene
        game.setActiveScene(game.addScene(splashScreen));

        // add levels
        levels.add(new TestLevel(game));

        // add menu-screen scene
        game.addScene(menuScreen);*/

        //loadResources();
        //game.setActiveScene(game.addScene(new TestScene2()));

        new TestLevel(game);
    }

    public void loadResources() {
        Game game = mEngine.getGame();

        // Rope sprite
        game.textureManager.add("rope", game.getRenderer().addTexture(new float[]{1, 0.5f, 0, 1},
                Rope.STANDARD_SEGMENT_LENGTH, Rope.STANDARD_SEGMENT_THICKNESS));
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
