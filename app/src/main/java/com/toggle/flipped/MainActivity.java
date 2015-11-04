package com.toggle.flipped;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.GameActivity;

public class MainActivity extends GameActivity {

    @Override
    public void onGameStart() {
        Game game = mEngine.getGame();
        game.addScene(new TestScene());
        game.addScene(new TestScene2());
        game.addScene(new LevelTestScene());
        game.setActiveScene(1);
    }
}
