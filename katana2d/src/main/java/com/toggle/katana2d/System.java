package com.toggle.katana2d;

import java.util.ArrayList;

// Base System class which stores a list of entities and types of components it need for processing
public class System {

    // List of entities
    protected ArrayList<Entity> mEntities = new ArrayList<>();

    // List of classes of component the system needs for processing
    protected final Class[] mComponents;

    // Base system classes must call super(...) on their constructors
    // passing the types of components they support/need
    public System(Class[] components) {
        mComponents = components;
    }

    // Add an entity to this system, checking if the entity contains necessary components
    public void addEntity(Entity entity) {
        boolean toAdd = true;
        for (Class component: mComponents) {
            if (!entity.has(component))
                toAdd = false;
        }
        if (toAdd) {
            mEntities.add(entity);
            onEntityAdded(entity);
        }
    }

    // Some base methods that the derived classes may override

    public void onEntityAdded(Entity entity) {}
    public void init() {}
    public void update(float dt) {}
    public void draw(float interpolation) {}

    public void onPause() {

    }
    public void onResume() {

    }
}
