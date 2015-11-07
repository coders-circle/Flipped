package com.toggle.flipped;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.Scene;

import org.json.JSONObject;

// Custom implementation for loading specific sprites, entities etc.
public interface CustomLoader {
    boolean loadSprite(Game game, String spriteName, JSONObject sprite);
    boolean loadEntity(Scene scene, String entityName, JSONObject entity);
}
