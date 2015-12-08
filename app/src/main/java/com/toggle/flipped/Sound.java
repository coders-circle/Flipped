package com.toggle.flipped;

import android.content.Context;
import com.toggle.katana2d.Component;

import java.util.ArrayList;
import java.util.List;

public class Sound implements Component {
    List<SoundSource> soundSources = new ArrayList<>();

    public static final int AMBIANCE        = 0b1;
    public static final int JUMP_START      = 0b10;
    public static final int JUMP_END        = 0b100;
    public static final int TOMBSTONE_RISE  = 0b1000;
    public static final int TOMBSTONE_FALL  = 0b10000;
    public static final int DOOR_RISE       = 0b100000;
    public static final int DOOR_FALL       = 0b1000000;
    public static final int EXPLOSION       = 0b10000000;
    public static final int LEVER_PUSH      = 0b100000000;
    // state can have multiple value of the above constants
    // e.g. state = AMBIANCE|JUMP_TAKEOFF
    public int state;

    Sound(){
        state = 0;
    }

    public void addState(int state){
        this.state |= state;
    }
    public void removeState(int state){
        this.state &= (~state);
    }

    // 'type' can be one of the above constants
    // i.e. AMBIANCE, JUMP_TAKEOFF, etc
    void addSource(Context context, int resID, int type){
        SoundSource src = new SoundSource();
        src.initialize(context, resID, type);
        soundSources.add(src);
    }
}
