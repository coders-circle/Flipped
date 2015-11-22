package com.toggle.flipped;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.GameActivity;
import com.toggle.katana2d.Texture;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends GameActivity {

    private List<Level> mLevels = new ArrayList<>();
    private Level mActiveLevel;

    @Override
    public void onGameStart() {
        Game game = mEngine.getGame();

        game.textureManager.add("rope", game.getRenderer().addTexture(new float[]{1,0.5f,0,1},
                Rope.STANDARD_SEGMENT_LENGTH, Rope.STANDARD_SEGMENT_THICKNESS));
        TestLevel testLevel = new TestLevel(game);
        //game.setActiveScene(game.addScene(new TestScene2()));
    }
}
