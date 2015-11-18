package com.toggle.katana2d;

import android.opengl.GLES20;

public class Texture {
    public int textureId;
    public int resourceId = -1;

    // The color to blend with the texture
    public float[] color = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

    // default size and origin of the texture
    public float width, height, originX, originY;

    public Texture(int textureId, float width, float height) {
        this(textureId, new float[]{1,1,1,1}, width, height);
    }

    public Texture(int textureId, float[] color, float width, float height) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
        this.color = color;
        originX = width/2;
        originY = height/2;
    }

    // draw the texture
    public void draw(GLRenderer renderer,float x, float y, float angle) {
        draw(renderer, x, y, angle, 0, 0, 1, 1);
    }

    // draw clipped texture: all parameters are to be in texture-space i.e. in the range of [0, 1]
    public void draw(GLRenderer renderer, float x, float y, float angle, float clipX, float clipY, float clipW, float clipH) {
        GLES20.glUseProgram(renderer.mSpriteProgram);
        GLES20.glVertexAttribPointer(renderer.mSpritePositionHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, renderer.mSpriteVertexBuffer);

        GLES20.glUniform4f(renderer.mSpriteClipHandle, clipX, clipY, clipW, clipH);
        renderer.setSpriteTransform(x, y, width, height, angle, originX, originY);
        GLES20.glUniform4fv(renderer.mSpriteColorHandle, 1, color, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // sample-0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, renderer.mSpriteIndexBuffer);
    }

}
