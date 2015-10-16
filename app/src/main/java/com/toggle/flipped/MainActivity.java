package com.toggle.flipped;

import com.toggle.katana2d.GLSprite;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.GameActivity;

public class MainActivity extends GameActivity {

    @Override
    public void onGameStart() {
        Game game = mEngine.getGame();
        // Yellow colored sprite
        game.spriteManager.add(
                "yellow_spr",
                new GLSprite(game.getRenderer(), null, new float[]{1, 1, 0, 1}, 64, 128)
        );
        // Test sprite from image
        game.spriteManager.add(
                "test_spr",
                new GLSprite(game.getRenderer(), game.getRenderer().loadTexture(R.drawable.test_spr), 124 - 40, 106)
        );


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