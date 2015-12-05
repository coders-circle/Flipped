package com.toggle.flipped;

import com.toggle.katana2d.Component;
import com.toggle.katana2d.Entity;

public class Burner implements Component {

    float position = 8;
    Entity emitter;
    boolean reflect = false;

    boolean source = true;
    float fullLife = -1;
    float life = 0;
    boolean isBurning = true;

    float sourceWidth=0, sourceHeight=0;    // meters

    public Burner(Entity emitter) {
        this.emitter = emitter;
    }
}
