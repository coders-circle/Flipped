package com.toggle.flipped;

import com.toggle.katana2d.Component;

import org.jbox2d.dynamics.Fixture;

import java.util.ArrayList;
import java.util.List;

// A wind source that applies wind force to specific
// rectangular area
public class WindSource implements Component {

    // The area to apply wind
    public final float max_distance;
    public final float width;

    // Set active to false to disable wind force
    public boolean active = true;

    // The strength of the wind
    // Note that the mass of a body and its distance from the source
    // also affects the final strength on it.
    public float force = 100f;

    public WindSource(float force, float max_distance, float width) {
        this.max_distance = max_distance;
        this.width = width;
        this.force = force;
    }

    public Fixture sensor;

    // List of all fixtures inside the wind area
    public List<Fixture> bodies = new ArrayList<>();
}
