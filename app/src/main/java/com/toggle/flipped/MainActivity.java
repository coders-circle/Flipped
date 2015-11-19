package com.toggle.flipped;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.GameActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GameActivity {

    private List<Level> mLevels = new ArrayList<>();
    private Level mActiveLevel;

    @Override
    public void onGameStart() {
        Game game = mEngine.getGame();
        //TestLevel testLevel = new TestLevel(game);
        game.setActiveScene(game.addScene(new TestScene2()));
    }
}
