package com.toggle.flipped;

import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.Scene;

import com.badlogic.gdx.physics.box2d.World;

import org.json.JSONObject;

// Custom implementation for loading specific sprites, entities etc.
public interface CustomLoader {
    boolean loadSprite(Game game, String spriteName, JSONObject sprite);
    Entity loadEntity(Scene scene, World world, String entityName, JSONObject entity);
}
