package com.toggle.katana2d;

import static java.lang.System.nanoTime;

public class Timer {
    private static final int ONE_SECOND = 1000000000;

    public Timer(double targetFPS) {
        reset(targetFPS);
    }

    // Reset the timer with given target-FPS
    public void reset(double targetFPS) {
        mLeftOver = 0.0;
        mTotalTime = 0.0;
        mLastTime = nanoTime();
        mTarget = ONE_SECOND / targetFPS;
        mFps = mFrameCounter = mSecondCounter = 0;
    }

    // Get actual FPS calculated
    public int getFPS() {
        return mFps;
    }
    // Get total time elapsed since reset
    public double getTotalTime() { return mTotalTime; }

    public void Update(TimerCallback callback) {

        // get current and delta times
        double currentTime = nanoTime();
        double deltaTime = currentTime - mLastTime;
        mLastTime = currentTime;

        // second counter is used to keep track whether we have crossed a second
        mSecondCounter += deltaTime;

        // deltaTime exceeding 1 second can give very bad results,
        // though will only happen if game or device is very very slow
        if (deltaTime > ONE_SECOND)
            deltaTime = ONE_SECOND;

        // stop accumulation of small error
        if (Math.abs(deltaTime - mTarget) < 0.000004)
            deltaTime = mTarget;

        // add deltaTime and leftOver time from previous frame
        mLeftOver += deltaTime;

        // check if we have passed one more frame this second
        // IMPORTANT: don't call this for each update call below inside the while loop below
        // as more calls needed means more time passed, which means slower device, which means less FPS not more
        if (mLeftOver >= mTarget)
            mFrameCounter++;

        // now update as much time as needed with fixed time-step
        while (mLeftOver >= mTarget) {
            mTotalTime += mTarget;
            mLeftOver -= mTarget;
            callback.update(mTarget/ONE_SECOND);
        }

        // calculate FPS using frameCounter and secondCounter
        if (mSecondCounter >= ONE_SECOND) {
            mFps = mFrameCounter;
            mFrameCounter = 0;
            mSecondCounter %= ONE_SECOND;
        }

    }

    private double mLastTime, mTarget, mLeftOver, mTotalTime;
    private int mFps, mFrameCounter, mSecondCounter;
}