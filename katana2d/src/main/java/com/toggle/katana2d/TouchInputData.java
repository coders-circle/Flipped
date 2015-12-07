package com.toggle.katana2d;

import android.util.SparseArray;

public class TouchInputData {

    public Pointer tap = new Pointer();
    public SparseArray<Pointer> pointers = new SparseArray<>();
    public static class Pointer {
        public float x;
        public float y;
        public float dx, vx;
        public float dy, vy;

        public float downTime = 0;
    }
}
