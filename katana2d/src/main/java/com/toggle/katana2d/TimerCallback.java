package com.toggle.katana2d;

public interface TimerCallback {
    // update callback with delta-time as time between calls as parameter
    void update(float deltaTime);
    void draw(float interpolation);
}
