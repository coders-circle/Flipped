package com.toggle.katana2d;

public class Background implements Component {
    public Texture mTexture;
    public final float distance;
    public float x = 0;
    public float y = 0;
    public float offsetX=0, offsetY=0;

    public Background(Texture texture, float zOrder) {
        mTexture = texture;
        mTexture.originX = 0;
        mTexture.originY = 0;
        distance = (1 - (BackgroundSystem.MAX_Z - zOrder) / BackgroundSystem.MAX_Z);
    }

    public Background(Texture texture, float zOrder, float offsetX, float offsetY) {
        this(texture, zOrder);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
