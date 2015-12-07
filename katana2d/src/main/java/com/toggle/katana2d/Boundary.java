package com.toggle.katana2d;

/**
 * Created by Ankit on 12/7/2015.
 */
public class Boundary {
    Boundary(){
        left = right = top = bottom = 0.0f;
    }
    public float left, right, top, bottom;
    public boolean hitTest(float x, float y){
        return ((x >= left && x <= right) && (y >= top && y <= bottom));
    }
    Boundary(float l, float t, float r, float b){
        left = l;
        right = r;
        top = t;
        bottom = b;
    }
}
