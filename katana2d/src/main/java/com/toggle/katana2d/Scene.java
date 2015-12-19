package com.toggle.katana2d;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    protected Game mGame;
    public int sceneId; // id corresponding to collection stored in parent Game class

    // List of all systems
    protected List<System> mSystems = new ArrayList<>();
    // List of all entities
    protected List<Entity> mEntities = new ArrayList<>();

    // Add entity to all valid systems
    public void addEntity(Entity entity) {
        mEntities.add(entity);
        for (System system : mSystems) {
            system.addEntity(entity);
        }
    }

    public void init(Game game) {
        mGame = game;
        mSystems.clear();
        onInit();

        // initialize all systems
        for (System system : mSystems) {
            system.init();
        }
    }

    protected void onInit() {
    }

    public void update(float deltaTime) {
        // update all systems
        for (System system : mSystems) {
            system.update(deltaTime);
        }

        onUpdate(deltaTime);
    }

    protected void onUpdate(float deltaTime) {
    }

    public void draw() {

        // call draw method of all systems
        for (System system : mSystems) {
            system.draw();
        }

        onDraw();
    }

    public void postDraw() {
        // call post draw method of all systems
        for (System system : mSystems) {
            system.postDraw();
        }

        onPostDraw();
    }

    public void onPostDraw() {

    }

    protected void onDraw() {
    }

    public void onPause() {
        for (System s: mSystems) {
            s.onPause();
        }
    }
    public void onResume() {
        for (System s: mSystems) {
            s.onResume();
        }}

    public Game getGame() {
        return mGame;
    }

    public void onActiveStateChanged(boolean isActive) {
    }
}
