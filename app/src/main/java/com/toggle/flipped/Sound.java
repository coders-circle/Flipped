package com.toggle.flipped;

import android.content.Context;
import com.toggle.katana2d.Component;

import java.util.ArrayList;
import java.util.List;

public class Sound implements Component {
    List<SoundSource> soundSources = new ArrayList<>();

    public static final int AMBIANCE = 0b1;
    public static final int JUMP_START = 0b10;
    public static final int JUMP_END = 0b100;

    // state can have multiple value of the above constants
    // e.g. state = AMBIANCE|JUMP_TAKEOFF
    public int state;

    Sound(){
        state = 0;
    }

    // 'type' can be one of the above constants
    // i.e. AMBIANCE, JUMP_TAKEOFF, etc
    void addSource(Context context, int resID, int type){
        SoundSource src = new SoundSource();
        src.initialize(context, resID, type);
        soundSources.add(src);
    }
}
