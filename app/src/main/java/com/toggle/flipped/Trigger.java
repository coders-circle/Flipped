package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.toggle.katana2d.Component;
import com.toggle.katana2d.Entity;

import java.util.ArrayList;
import java.util.List;

public class Trigger implements Component {

    public float load = 0;
    public boolean isPressed = false;
    public float displacement = 0;
    public Vector2 originalPosition;
    public List<Fixture> loads = new ArrayList<>();

    public enum Type {LEVER, BUTTON}
    public Type type = Type.LEVER;
    public boolean getStatus() {
        return status;
    }

    public String tag;
    public Object object;

    public interface Listener {
        void onTriggered(boolean status);
    }

    private boolean status = false;
    public float leverPosition = 0;
    public List<Listener> listeners = new ArrayList<>();

    public void trigger() {
        status = !status;
        for (Listener listener : listeners) {
            listener.onTriggered(status);
        }
    }
}
