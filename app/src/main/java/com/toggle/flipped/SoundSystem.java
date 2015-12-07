package com.toggle.flipped;

import android.util.Log;

import com.toggle.katana2d.*;

public class SoundSystem extends com.toggle.katana2d.System {
    public SoundSystem(){
        super(new Class[]{Sound.class});
    }

    @Override
    public void update(float dt)
    {
        for(Entity entity: mEntities){
            Sound sound = entity.get(Sound.class);
            for(SoundSource soundSource: sound.soundSources){
                if((soundSource.type() & sound.state) != 0){
                    if(!soundSource.isFinished() && !soundSource.isPlaying()){
                        soundSource.start();
                    }

                    if(soundSource.isPlaying()){
                        // TODO: update position of the sound source using following code
                        // soundSource.updatePosition(x, y);
                        // where (x, y) is relative coordinate of their screen position
                    }
                }
                if(soundSource.type() == Sound.AMBIANCE && !soundSource.isPlaying()){
                    soundSource.setLooping(true);
                    soundSource.start();
                }
            }
        }
    }
}
