package com.toggle.flipped;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;

import com.badlogic.gdx.physics.box2d.World;

import org.json.JSONObject;

// Custom implementation for loading specific sprites, entities etc.
public interface CustomLoader {
    boolean loadSprite(Game game, String spriteName, JSONObject sprite);
    boolean loadEntity(Entity entity, World world, String entityName, JSONObject entityJson);
    boolean addComponent(World world, Entity entity, String compName, JSONObject component, JSONObject components);
}
