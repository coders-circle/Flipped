package com.toggle.katana2d;

public class Background implements Component {
    public Texture mTexture;
    public float z;
    public float x = 0;
    public float lastX = 0;

    public Background(Texture texture, float zOrder) {
        mTexture = texture;
        mTexture.originX = 0;
        mTexture.originY = 0;
        z = zOrder;
    }
}
