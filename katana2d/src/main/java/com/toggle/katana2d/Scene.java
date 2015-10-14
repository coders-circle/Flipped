package com.toggle.katana2d;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    protected Game mGame;

    // List of all systems
    protected List<System> mSystems = new ArrayList<>();

    // Add entity to all valid systems
    public void addEntity(Entity entity) {
        for (System system : mSystems) {
            system.addEntity(entity);
        }
    }

    public void init(Game game) {
        mGame = game;
        onInit();

        // initialize all systems
        for (System system : mSystems) {
            system.init();
        }
    }

    protected void onInit() {
    }

    public void update(double deltaTime) {
        // update all systems
        for (System system : mSystems) {
            system.update(deltaTime);
        }

        onUpdate(deltaTime);
    }

    protected void onUpdate(double deltaTime) {
    }

    public void draw() {
        // call draw method of all systems
        for (System system : mSystems) {
            system.draw();
        }

        onDraw();
    }

    protected void onDraw() {
    }

    /*TODO: Pass input events to all systems.*/
}
