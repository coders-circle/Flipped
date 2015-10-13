package com.toggle.katana2d;

import android.opengl.GLES20;

public class GLSprite {
    // A reference to the renderer
    public final GLRenderer mRenderer;

    // The texture to draw this sprite
    public Texture mTexture;

    // The color to blend with the texture
    public float[] mColor = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

    // size and origin of the sprite
    public float width, height, originX = 0, originY = 0;

    // Create sprite with a texture; set textureId = null for white texture
    public GLSprite(GLRenderer renderer, Texture texture, float width, float height) {
        mRenderer = renderer;
        this.width = width;
        this.height = height;
        if (texture == null)
            mTexture = mRenderer.mWhiteTexture;
        else
            mTexture = texture;
    }

    // Create sprite with a texture and a color; set textureId = null for using color only
    public GLSprite(GLRenderer renderer, Texture texture, float[] color, float width, float height) {
        this(renderer, texture, width, height);
        mColor = color;
    }

    // draw the sprite
    public void draw(float x, float y, float angle) {
        draw(x, y, angle, 0, 0, 1, 1);
    }

    // draw clipped sprite: all parameters are to be in texture-space i.e. in the range of [0, 1]
    public void draw(float x, float y, float angle, float clipX, float clipY, float clipW, float clipH) {
        GLES20.glUniform4f(mRenderer.mClipHandle, clipX, clipY, clipW, clipH);
        mRenderer.setSpriteTransform(x, y, width, height, angle, originX, originY);
        GLES20.glUniform4fv(mRenderer.mColorHandle, 1, mColor, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // sample-0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.textureId);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mRenderer.indexBuffer);
    }
}
