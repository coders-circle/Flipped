package com.toggle.flipped;

import com.toggle.katana2d.GLRenderer;
import com.toggle.katana2d.ParticleSystem;
import com.toggle.katana2d.RenderSystem;
import com.toggle.katana2d.Scene;
import com.toggle.katana2d.physics.PhysicsSystem;

public class LevelTestScene extends Scene {

    @Override
    public void onInit() {
        PhysicsSystem physicsSystem = new PhysicsSystem();

        // Add the systems
        mSystems.add(new RenderSystem());
        mSystems.add(physicsSystem);

        String level = GLRenderer.getRawFileText(mGame.getActivity(), R.raw.basketball_test);
        LevelLoader loader = new LevelLoader();
        loader.load(mGame, level);

        loader.loadWorld("World1", this, physicsSystem.getWorld());
    }
}
