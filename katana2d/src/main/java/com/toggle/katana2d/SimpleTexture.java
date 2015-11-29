package com.toggle.katana2d;

public class SimpleTexture extends Texture {

    public int textureId;

    public SimpleTexture(int textureId, float width, float height) {
        this(textureId, new float[]{1,1,1,1}, width, height);
    }

    public SimpleTexture(int textureId, float[] color, float width, float height) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void draw(GLRenderer renderer, float x, float y, float z, float angle, float scaleX, float scaleY, float clipX, float clipY, float clipW, float clipH) {
        draw(textureId, renderer, x, y, z, angle, scaleX, scaleY, clipX, clipY, clipW, clipH);
    }

}
