package com.toggle.katana2d;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    protected Game mGame;

    public Scene(Game game) {
        mGame = game;
    }

    // The list of all systems
    protected List<System> mSystems = new ArrayList<>();

    // Method to add entity to all valid systems
    public void addEntity(Entity entity) {
        for (System system : mSystems) {
            system.addEntity(entity);
        }
    }

    public void init() {
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
}
