package com.toggle.katana2d;

public class Background implements Component {
    public Texture mTexture;
    public final float distance;
    public float x = 0;
    public float y = 0;
    public float lastX = 0, lastY = 0;

    public Background(Texture texture, float zOrder) {
        mTexture = texture;
        mTexture.originX = 0;
        mTexture.originY = 0;
        distance = (1 - (BackgroundSystem.MAX_Z - zOrder) / BackgroundSystem.MAX_Z);
    }
}
