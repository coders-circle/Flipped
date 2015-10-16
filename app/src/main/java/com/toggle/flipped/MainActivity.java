package com.toggle.flipped;

import com.toggle.katana2d.GLSprite;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.GameActivity;

public class MainActivity extends GameActivity {

    @Override
    public void onGameStart() {
        Game game = mEngine.getGame();
        game.addScene(new TestScene());
        game.setActiveScene(0);
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