package com.toggle.katana2d;

import android.opengl.GLES20;

public class Texture {
    public int resourceId = -1;     // needed to reload the texture

    // The color to blend with the texture
    public float[] color = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

    // default size and origin of the texture
    public float width, height, originX = 0.5f, originY=0.5f;   // origin is in normalized system: [0..1]

    // draw the texture
    public void draw(GLRenderer renderer,float x, float y, float z, float angle, float scaleX, float scaleY) {
        draw(renderer, x, y, z, angle, scaleX, scaleY, 0, 0, 1, 1);
    }

    // draw clipped texture: all parameters are to be in texture-space i.e. in the range of [0, 1]
    public void draw(int textureId, GLRenderer renderer, float x, float y, float z, float angle, float scaleX, float scaleY, float clipX, float clipY, float clipW, float clipH) {
        GLES20.glUseProgram(renderer.mSpriteProgram);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, renderer.mSpriteBO[0]);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, renderer.mSpriteBO[1]);
        GLES20.glVertexAttribPointer(renderer.mSpritePositionHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, 0);

        GLES20.glUniform4f(renderer.mSpriteClipHandle, clipX, clipY, clipW, clipH);
        renderer.setSpriteTransform(x, y, z, width * scaleX, height * scaleY, angle, originX, originY);
        GLES20.glUniform4fv(renderer.mSpriteColorHandle, 1, color, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // sample-0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void draw(GLRenderer renderer, float x, float y, float z, float angle, float scaleX, float scaleY, float clipX, float clipY, float clipW, float clipH)
    {}
}
