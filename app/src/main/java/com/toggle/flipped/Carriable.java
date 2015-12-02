package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.Component;
import com.toggle.katana2d.Entity;

import java.util.ArrayList;
import java.util.List;

public class Carriable implements Component {
    // while carrying
    public Vector2 position;
    public float angle;

    // while picking
    public List<Vector2> positions = new ArrayList<>();
    public List<Float> angles = new ArrayList<>();

    public Entity carrier; // Entity who is carrying/picking this object

    // is being picked, carried or nothing?
    enum State {NOTHING, PICKED, CARRIED}
    public State state = State.NOTHING;
}
