package com.toggle.katana2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    // We store the context of the MainActivity, which can be useful
    // in many Android functions
    private Context mContext;
    // Game object
    private Game mGame;
    GLRenderer(Context context, Game game) { mContext = context; mGame = game;}

    // Touch input data;
    public TouchInputData touchInputData;

    // GLSL program objects
    public int mSpriteProgram;
    public int mPointSpriteProgram;

    // Uniform handles
    public int mSpriteColorHandle;
    public int mSpriteMVPMatrixHandle;
    public int mSpriteClipHandle;

    public int mPointSpriteMVPMatrixHandle;

    // vertex buffer data
    private static float mSquareCoords[] = {
            0, 1,
            0, 0,
            1, 0,
            1, 1,
    };
    // index buffer data
    private static short mSquareDrawOrder[] = { 0, 2, 1, 0, 3, 2 };

    // vertex and index buffers
    public FloatBuffer mSpriteVertexBuffer;
    public ShortBuffer mSpriteIndexBuffer;

    // VBO and IBO
    public final int[] mSpriteBO = new int[2];

    // Attribute handles
    public int mSpritePositionHandle;
    public int mPointSpritePositionHandle;
    public int mPointSpriteColorHandle;
    public int mPointSpriteSizeHandle;

    // Transformation matrices
    public final float[] mMVPMatrix = new float[16];
    public final float[] mProjectionMatrix = new float[16];
    public final float[] mModelMatrix = new float[16];

    // A white texture to use when no texture is selected
    public int mWhiteTextureId;
    public int mFuzzyTextureId;

    // Camera to defining view position and angle
    private Camera mCamera = new Camera();

    private float mBackR, mBackG, mBackB;
    // Set background color
    public void setBackgroundColor(float r, float g, float b) {
        mBackR = r;
        mBackG = g;
        mBackB = b;
    }

    public void setAdditiveBlending() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
    }

    public void setAlphaBlending() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void disableDepth() {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void enableDepth() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Enable blending for transparency
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Enable z buffer
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Enable scissoring
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);

        // Set the background frame color
        GLES20.glClearColor(100.0f / 255, 149.0f / 255, 237.0f / 255, 1.0f);

        // Compile the sprite shaders
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Utilities.getRawFileText(mContext, R.raw.vs_sprite));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Utilities.getRawFileText(mContext, R.raw.fs_sprite));

        // Link the shaders to a program
        mSpriteProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mSpriteProgram, vertexShader);
        GLES20.glAttachShader(mSpriteProgram, fragmentShader);
        GLES20.glLinkProgram(mSpriteProgram);
        GLES20.glUseProgram(mSpriteProgram);

        // Get the attribute and uniform handles
        mSpritePositionHandle = GLES20.glGetAttribLocation(mSpriteProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mSpritePositionHandle);
        mSpriteColorHandle = GLES20.glGetUniformLocation(mSpriteProgram, "uColor");
        mSpriteMVPMatrixHandle = GLES20.glGetUniformLocation(mSpriteProgram, "uMVPMatrix");
        mSpriteClipHandle = GLES20.glGetUniformLocation(mSpriteProgram, "uClip");

        // Create the vertex buffer
        mSpriteVertexBuffer = ByteBuffer.allocateDirect(mSquareCoords.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mSpriteVertexBuffer.put(mSquareCoords).position(0);

        // Create the index buffer
        mSpriteIndexBuffer = ByteBuffer.allocateDirect(mSquareDrawOrder.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();
        mSpriteIndexBuffer.put(mSquareDrawOrder).position(0);

        // Create VBO and IBO
        GLES20.glGenBuffers(2, mSpriteBO, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mSpriteBO[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mSpriteVertexBuffer.capacity() * 4, mSpriteVertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mSpriteBO[1]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mSpriteIndexBuffer.capacity() * 2, mSpriteIndexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        // get the texture uniform handle and set it to use the sample-0
        int texHandle = GLES20.glGetUniformLocation(mSpriteProgram, "uTexture");
        GLES20.glUniform1i(texHandle, 0);

        // Shaders for point sprites
        int pVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Utilities.getRawFileText(mContext, R.raw.vs_point_sprite));
        int pFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Utilities.getRawFileText(mContext, R.raw.fs_point_sprite));

        // Link the shaders to a program
        mPointSpriteProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mPointSpriteProgram, pVertexShader);
        GLES20.glAttachShader(mPointSpriteProgram, pFragmentShader);
        GLES20.glLinkProgram(mPointSpriteProgram);
        GLES20.glUseProgram(mPointSpriteProgram);

        // Get the attribute and uniform handles
        mPointSpritePositionHandle = GLES20.glGetAttribLocation(mPointSpriteProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPointSpritePositionHandle);
        mPointSpriteColorHandle = GLES20.glGetAttribLocation(mPointSpriteProgram, "vColor");
        GLES20.glEnableVertexAttribArray(mPointSpriteColorHandle);
        mPointSpriteSizeHandle = GLES20.glGetAttribLocation(mPointSpriteProgram, "vSize");
        GLES20.glEnableVertexAttribArray(mPointSpriteSizeHandle);
        mPointSpriteMVPMatrixHandle = GLES20.glGetUniformLocation(mPointSpriteProgram, "uMVPMatrix");

        // get the texture uniform handle and set it to use the sample-0
        int pTexHandle = GLES20.glGetUniformLocation(mPointSpriteProgram, "uTexture");
        GLES20.glUniform1i(pTexHandle, 0);

        // Load the standard textures
        mWhiteTextureId = loadTexture(R.drawable.white);
        mFuzzyTextureId = loadTexture(R.drawable.fuzzy_circle);

        // Reload the user created textures if any
        for (Texture t: mTextures)
            reloadTexture(t);

        // Initialize the Engine
        mGame.init();
    }

    public final float[] mViewMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 unused) {

        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        GLES20.glClearColor(0, 0, 0, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);

        // View transformations
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.translateM(mViewMatrix, 0, width / 2, height / 2, 0);
        Matrix.rotateM(mViewMatrix, 0, mCamera.angle, 0, 0, 1);
        Matrix.translateM(mViewMatrix, 0, -width / 2, -height / 2, 0);
        Matrix.translateM(mViewMatrix, 0, -mCamera.x, -mCamera.y, 0);

        GLES20.glClearColor(mBackR, mBackG, mBackB, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mGame.newFrame();
    }

    public final int width = 480, height = 320;

    public Camera getCamera() { return mCamera; }

    @Override
    public void onSurfaceChanged(GL10 unused, int dev_width, int dev_height) {
        float ar = (float)dev_width/(float)dev_height;
        float cx = 0, cy = 0, scale;

        // Fit one dimension and maintain the ratio with other dimension
        float aspect_ratio = (float) width / (float) height;
        // if aspect-ratio of device is greater than what is desired, fit the height
        if (ar > aspect_ratio) {
            scale = (float)dev_height/(float)height;
            cx = (dev_width - width*scale)/2.0f;
        }
        // otherwise fit the width
        else {
            scale = (float)dev_width/(float)width;
            cy = (dev_height - height*scale)/2.0f;
        }

        // Set the viewport
        GLES20.glViewport((int) cx, (int) cy, (int) (width * scale), (int) (height * scale));

        // Set the orthographic projection matrix.
        Matrix.orthoM(mProjectionMatrix, 0, 0, width, height, 0, -100, 100);

        // Scissor the viewport
        GLES20.glScissor((int) cx, (int) cy, (int) (width * scale), (int) (height * scale));
    }

    // Set transform for drawing the rectangle.
    public void setSpriteTransform(float posX, float posY, float z, float scaleX, float scaleY, float angle, float originX, float originY) {
        // calculate transformation matrix mMVPMatrix that will transform the vertices of the square

        // mModelMatrix = Translate(posX, posY) * Rotate(angle) * Translate(-originX, -originY) * Scale(scaleX, scaleY)
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, posX, posY, z);
        Matrix.rotateM(mModelMatrix, 0, angle, 0, 0, 1);
        Matrix.scaleM(mModelMatrix, 0, scaleX, scaleY, 1);
        Matrix.translateM(mModelMatrix, 0, -originX, -originY, 0);

        // mMVPMatrix = mProjectionMatrix * mViewMatrix * mModelMatrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // finally set the value of mvpMatrix uniform to the mMVPMatrix
        GLES20.glUniformMatrix4fv(mSpriteMVPMatrixHandle, 1, false, mMVPMatrix, 0);
    }

    public void setPointSpriteTransform(float posX, float posY, float angle) {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, posX, posY, 0);
        Matrix.rotateM(mModelMatrix, 0, angle, 0, 0, 1);

        // mMVPMatrix = mProjectionMatrix * mViewMatrix * mModelMatrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // finally set the value of mvpMatrix uniform to the mMVPMatrix
        GLES20.glUniformMatrix4fv(mPointSpriteMVPMatrixHandle, 1, false, mMVPMatrix, 0);
    }

    // Create shader object from shader program
    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] != GLES20.GL_TRUE) {
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Could not compile program: "
                    + GLES20.glGetShaderInfoLog(shader) + " | " + shaderCode);
        }
        return shader;
    }

    private List<Texture> mTextures = new ArrayList<>();

    private int loadTexture(Bitmap bitmap) {
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            if (bitmap == null) {
                throw new RuntimeException("Error decoding bitmap");
            }
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }
        else {
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];
    }

    private int loadTexture(int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId);
        int texid = loadTexture(bitmap);
        bitmap.recycle();
        return texid;
    }

    // create texture from a resource image
    public Texture addTexture(int resourceId, float width, float height) {
        Texture t = new Texture(loadTexture(resourceId), width, height);
        t.resourceId = resourceId;
        mTextures.add(t);
        return t;
    }

    // create texture from bitmap
    public Texture addTexture(Bitmap bitmap, float width, float height) {
        Texture t = new Texture(loadTexture(bitmap), width, height);
        t.bitmap = bitmap;
        mTextures.add(t);
        return t;
    }

    // create texture from color
    public Texture addTexture(float[] color, float width, float height) {
        Texture t = new Texture(mWhiteTextureId, color, width, height);
        t.resourceId = -1;
        mTextures.add(t);
        return t;
    }

    // create texture from color and resource image
    public Texture addTexture(int resourceId, float[] color, float width, float height) {
        Texture t = new Texture(loadTexture(resourceId), color, width, height);
        t.resourceId = resourceId;
        mTextures.add(t);
        return t;
    }

    private void reloadTexture(Texture t) {
        if (t.resourceId >= 0)
            t.textureId = loadTexture(t.resourceId);
        else if (t.bitmap != null)
            t.textureId = loadTexture(t.bitmap);
    }
}

