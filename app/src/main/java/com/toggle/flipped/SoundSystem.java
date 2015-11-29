package com.toggle.flipped;

import com.toggle.katana2d.*;

/**
 * Created by Ankit on 11/27/2015.
 */
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
                    if(soundSource.isFinished() == false && soundSource.isPlaying() == false){
                        soundSource.start();
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
}
