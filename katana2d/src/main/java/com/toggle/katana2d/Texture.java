package com.toggle.katana2d;

public class Texture {
    public int textureId;
    public int width;
    public int height;
    public int resourceId;

    Texture(int textureId, int width, int height, int resourceId) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
        this.resourceId = resourceId;
    }

    Texture() {}
}
