package com.toggle.flipped;

import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;

import java.util.List;

public class MenuScreen extends Scene {

    private List<Level> mLevels;

    public MenuScreen(List<Level> levels) {
        mLevels = levels;
    }

    @Override
    public void onInit() {
        mSystems.add(new RenderSystem(mGame.getRenderer()));

    }
}
