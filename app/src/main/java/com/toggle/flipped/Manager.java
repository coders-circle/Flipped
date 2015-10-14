package com.toggle.flipped;

import com.toggle.katana2d.GLSprite;

import java.util.ArrayList;
import java.util.List;

// Manages all common data like resources
public class Manager {

    // Makes this a singleton class
    private static Manager manager = new Manager();
    public static Manager getManager() { return manager; }

    // Common resources:

    // Sprites
    private List<GLSprite> sprites = new ArrayList<>();
    public int addSprite(GLSprite sprite) { sprites.add(sprite); return sprites.size()-1; }
    public GLSprite getSprite(int index) { return sprites.get(index); }
}
