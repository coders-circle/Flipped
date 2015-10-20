package com.toggle.katana2d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class SurfaceView extends GLSurfaceView {
    private TouchInputData mTouchInputData;

    public SurfaceView(Context context, GLRenderer renderer){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        mTouchInputData = renderer.touchInputData;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchInputData.x = e.getX();
                mTouchInputData.y = e.getY();
                mTouchInputData.dx = mTouchInputData.dy = 0;
                mTouchInputData.isTouchDown = true;
                break;
            case MotionEvent.ACTION_UP:
                mTouchInputData.isTouchDown = false;
                mTouchInputData.dx = 0;
                mTouchInputData.dy = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                float lastX = mTouchInputData.x;
                float lastY = mTouchInputData.y;
                mTouchInputData.x = e.getX();
                mTouchInputData.y = e.getY();
                mTouchInputData.dx = e.getX() - lastX;
                mTouchInputData.dy = e.getY() - lastY;
                break;
        }
        return true;
    }
}