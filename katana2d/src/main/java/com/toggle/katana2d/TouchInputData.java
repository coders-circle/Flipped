package com.toggle.katana2d;

import android.util.SparseArray;

public class TouchInputData {

    public Pointer tap = new Pointer();
    public SparseArray<Pointer> pointers = new SparseArray<>();
    public static class Pointer {
        // public boolean isTouchDown = false;
        public float x;
        public float y;
        public float dx;
        public float dy;
    }
}
