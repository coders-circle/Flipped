package com.toggle.katana2d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PointSprites {

    private GLRenderer mRenderer;
    public GLRenderer getRenderer() { return mRenderer; }

    public FloatBuffer mPointSpriteVertexBuffer;
    private int mTextureId;

    public static final int ELEMENTS_PER_POINT = 7;
    public static final int BYTES_PER_POINT = ELEMENTS_PER_POINT * 4;

    public PointSprites(GLRenderer renderer, int textureId, int maxPoints) {
        mRenderer = renderer;

        mPointSpriteVertexBuffer = ByteBuffer.allocateDirect(maxPoints * BYTES_PER_POINT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mTextureId = textureId;
    }

    public void draw(float[] data, float x, float y, float angle, int numPoints) {
        if (data == null || data.length == 0)
            return;

        GLES20.glUseProgram(mRenderer.mPointSpriteProgram);
        mPointSpriteVertexBuffer.put(data);

        mPointSpriteVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mRenderer.mPointSpritePositionHandle, 2, GLES20.GL_FLOAT, false, BYTES_PER_POINT, mPointSpriteVertexBuffer);
        mPointSpriteVertexBuffer.position(2);
        GLES20.glVertexAttribPointer(mRenderer.mPointSpriteColorHandle, 4, GLES20.GL_FLOAT, false, BYTES_PER_POINT, mPointSpriteVertexBuffer);
        mPointSpriteVertexBuffer.position(2 + 4);
        GLES20.glVertexAttribPointer(mRenderer.mPointSpriteSizeHandle, 1, GLES20.GL_FLOAT, false, BYTES_PER_POINT, mPointSpriteVertexBuffer);

        mPointSpriteVertexBuffer.position(0);

        GLES20.glUseProgram(mRenderer.mPointSpriteProgram);
        mRenderer.setPointSpriteTransform(x, y, angle);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // sample-0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, numPoints);

    }

}
