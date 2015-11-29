package com.toggle.katana2d;

import android.graphics.Bitmap;

public class SimpleBitmapTexture extends SimpleTexture {

    public Bitmap bitmap = null;    // needed to reload the texture

    public SimpleBitmapTexture(int textureId, float width, float height) {
        super(textureId, width, height);
    }
}
