package com.toggle.katana2d;

import android.content.Context;
import android.opengl.GLSurfaceView;
//import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import java.lang.*;
import java.lang.System;

public class SurfaceView extends GLSurfaceView {
    private TouchInputData mTouchInputData = new TouchInputData();
    private GLRenderer mRenderer;

    public SurfaceView(Context context, GLRenderer renderer){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        renderer.touchInputData = mTouchInputData;
        mRenderer = renderer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        // get pointer index from the event object
        int pointerIndex = e.getActionIndex();
        // get pointer ID
        int pointerId = e.getPointerId(pointerIndex);

        int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // We have a new pointer. Add it to the list of pointers
                TouchInputData.Pointer pointer = new TouchInputData.Pointer();
                pointer.x = (e.getX(pointerIndex) - mRenderer.getViewportX()) / mRenderer.getViewportScale();
                pointer.y = (e.getY(pointerIndex) - mRenderer.getViewportY()) / mRenderer.getViewportScale();
                pointer.dx = pointer.vx = 0;
                pointer.dy = pointer.vy = 0;
                pointer.downTime = java.lang.System.nanoTime();
                mTouchInputData.pointers.put(pointerId, pointer);

                mTouchInputData.tap.x = pointer.x;
                mTouchInputData.tap.y = pointer.y;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // a pointer was moved
                for (int size = e.getPointerCount(), i = 0; i < size; i++) {
                    TouchInputData.Pointer pointer = mTouchInputData.pointers.get(e.getPointerId(i));
                    if (pointer != null) {
                        float lastX = pointer.x;
                        float lastY = pointer.y;
                        pointer.x = (e.getX(i) - mRenderer.getViewportX()) / mRenderer.getViewportScale();
                        pointer.y = (e.getY(i) - mRenderer.getViewportY()) / mRenderer.getViewportScale();
                        pointer.dx = pointer.x - lastX;
                        pointer.dy = pointer.y - lastY;
                        float newtime = System.nanoTime();
                        float dt = (newtime - pointer.downTime) / Timer.ONE_SECOND;
                        pointer.vx = pointer.dx / dt;
                        pointer.vy = pointer.dy / dt;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                mTouchInputData.pointers.remove(pointerId);
                break;
            }
        }
        return true;
    }
}