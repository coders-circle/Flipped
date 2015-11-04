package com.toggle.katana2d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class SurfaceView extends GLSurfaceView {
    private TouchInputData mTouchInputData = new TouchInputData();

    public SurfaceView(Context context, GLRenderer renderer){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        renderer.touchInputData = mTouchInputData;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        // get pointer index from the event object
        int pointerIndex = e.getActionIndex();
        // get pointer ID
        int pointerId = e.getPointerId(pointerIndex);

        switch (e.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mTouchInputData.x = e.getX();
//                mTouchInputData.y = e.getY();
//                mTouchInputData.dx = mTouchInputData.dy = 0;
//                mTouchInputData.isTouchDown = true;
//                break;
//            case MotionEvent.ACTION_UP:
//                mTouchInputData.isTouchDown = false;
//                mTouchInputData.dx = 0;
//                mTouchInputData.dy = 0;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float lastX = mTouchInputData.x;
//                float lastY = mTouchInputData.y;
//                mTouchInputData.x = e.getX();
//                mTouchInputData.y = e.getY();
//                mTouchInputData.dx = e.getX() - lastX;
//                mTouchInputData.dy = e.getY() - lastY;
//                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // We have a new pointer. Add it to the list of pointers
                TouchInputData.Pointer pointer = new TouchInputData.Pointer();
                pointer.x = e.getX(pointerIndex);
                pointer.y = e.getY(pointerIndex);
                pointer.dx = e.getX(pointerIndex);
                pointer.dy = e.getY(pointerIndex);
                mTouchInputData.pointers.put(pointerId, pointer);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // a pointer was moved
                for (int size = e.getPointerCount(), i = 0; i < size; i++) {
                    TouchInputData.Pointer pointer = mTouchInputData.pointers.get(e.getPointerId(i));
                    if (pointer != null) {
                        float lastX = pointer.x;
                        float lastY = pointer.y;
                        pointer.x = e.getX(i);
                        pointer.y = e.getY(i);
                        pointer.dx = pointer.x - lastX;
                        pointer.dy = pointer.y - lastY;
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