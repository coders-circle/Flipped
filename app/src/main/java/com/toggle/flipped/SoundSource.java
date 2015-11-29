package com.toggle.flipped;

import android.content.Context;
import android.media.MediaPlayer;

// note: a source at (+/- 1, y) will not be heard
// for source to be heard the x coordinate of position should be < |1|

public class SoundSource {
    private MediaPlayer mSrc;
    private float mX, mY;
    private float mListenerX, mListenerY;
    private float mStereoConst;
    private boolean mFinished;

    private int mType;

    SoundSource(){
        mX = 0.0f;
        mY = 0.0f;
        mListenerX = 0.0f;
        mListenerY = 0.0f;
        mStereoConst = 0.2f;
        mFinished = false;
    }

    public int type(){return mType;}

    public void initialize(Context context, int resID, int type){
        mSrc = MediaPlayer.create(context, resID);
        mType = type;
        mSrc.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mFinished = true;
            }
        });
    }
    public void setPosition(float x, float y){
        mX = x;
        mY = y;
    }

    // Currently experimental
    public void setListenerPosition(float x, float y){
        mListenerX = x;
        mListenerY = y;
    }

    public void start(){
        if(!mSrc.isPlaying()) {
            mFinished = false;
            mSrc.start();
        }
    }
    public boolean isFinished(){
        return mFinished;
    }
    public void restart(){
        this.stop();
        this.start();
    }
    public boolean isPlaying(){return mSrc.isPlaying();}
    public void stop(){
        mSrc.stop();
    }
    public void pause(){
        mSrc.pause();
    }

    public void setLooping(boolean looping){
        mSrc.setLooping(looping);
    }

    public void update(){
        if(mX >= 1.0f || mX <= -1.0f) {
            mSrc.setVolume(0.0f, 0.0f);
        }
        else {
            float dXL = (mX - mStereoConst) - mListenerX;
            float dXR = (mX + mStereoConst) - mListenerX;
            float dY = mY - mListenerY;
            float volL = 1.0f - (float) Math.sqrt(dXR * dXR + dY * dY);
            float volR = 1.0f - (float) Math.sqrt(dXL * dXL + dY * dY);
            volL = volL * volL;
            volR = volR * volR;
            mSrc.setVolume(Math.max(0.0f, volL), Math.max(0.0f, volR));
        }
    }

    public void updatePosition(float x, float y){
        this.setPosition(x, y);
        this.update();
    }
}
