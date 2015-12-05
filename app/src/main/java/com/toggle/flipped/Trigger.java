package com.toggle.flipped;

import com.toggle.katana2d.Component;

import java.util.ArrayList;
import java.util.List;

public class Trigger implements Component {

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
