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
                    if(!soundSource.isFinished() && !soundSource.isStarted()){
                        soundSource.start();
                        if((sound.state&Sound.TOMBSTONE_RISE) != 0){
                            Log.v("sound", "tomb");
                        }
                    }

                    if(soundSource.isFinished()){
                        sound.state = sound.state & (~soundSource.type());
                        soundSource.reset();
                    }

                    if(soundSource.isPlaying()){
                        // TODO: update position of the sound source using following code
                        // soundSource.updatePosition(x, y);
                        // where (x, y) is relative coordinate of their screen position
                    }
                }
            }
        }
    }

    public void onPause(){
        for(Entity entity: mEntities) {
            Sound sound = entity.get(Sound.class);
            for (SoundSource soundSource : sound.soundSources) {
                if(soundSource.isStarted()){
                    soundSource.pause();
                }
            }
        }
    }

    public void onResume(){
        for(Entity entity: mEntities) {
            Sound sound = entity.get(Sound.class);
            for (SoundSource soundSource : sound.soundSources) {
                if(soundSource.isStarted() && !soundSource.isFinished()){
                    soundSource.start();
                }
            }
        }
    }
}
