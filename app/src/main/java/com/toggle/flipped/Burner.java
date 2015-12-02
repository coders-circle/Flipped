package com.toggle.flipped;

import com.toggle.katana2d.Component;
import com.toggle.katana2d.Entity;

public class Burner implements Component {

    float position = 8;
    Entity emitter;
    boolean reflect = false;

    public Burner(Entity emitter) {
        this.emitter = emitter;
    }
}
