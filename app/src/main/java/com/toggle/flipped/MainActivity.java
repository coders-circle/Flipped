package com.toggle.flipped;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.GameActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GameActivity {

    private List<Level> mLevels = new ArrayList<>();
    private Level mActiveLevel;
    private CommonLoader mCommonLoader = new CommonLoader();

    @Override
    public void onGameStart() {
        Game game = mEngine.getGame();
        /*game.addScene(new TestScene());
        game.addScene(new TestScene2());*/

        //TestLevel testLevel = new TestLevel(game, mCommonLoader);
        //int i = game.addScene(testLevel.getActiveWorld());

        int i = game.addScene(new TestScene2());
        game.setActiveScene(i);
    }
}
