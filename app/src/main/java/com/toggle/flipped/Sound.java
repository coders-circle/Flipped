package com.toggle.flipped;

import android.content.Context;
import com.toggle.katana2d.Component;
import java.util.List;

/**
 * Created by Ankit on 11/27/2015.
 *
 */
public class Sound implements Component {
    List<SoundSource> soundSources;

    public static final int AMBIANCE = 0b1;
    public static final int JUMP_TAKEOFF = 0b10;
    public static final int JUMP_DROP = 0b100;

    // state can have multiple value of the above constants
    // e.g. state = AMBIANCE|JUMP_TAKEOFF
    public int state;

    Sound(){
        state = 0;
    }

    // 'type' can be one of the above constants
    // i.e. AMBIANCE, JUMP_TAKEOFF, etc
    void AddSource(Context context, int resID, int type){
        SoundSource src = new SoundSource();
        src.initialize(context, resID, type);
        soundSources.add(src);
    }
}
