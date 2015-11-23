package com.toggle.flipped;

import com.toggle.katana2d.Component;

import java.util.ArrayList;
import java.util.List;

public class Trigger implements Component {

    public interface Listener {
        void onTriggered(boolean status);
    }

    public boolean status = false;
    public List<Listener> listeners = new ArrayList<>();

    public void trigger() {
        status = !status;
        for (Listener listener : listeners) {
            listener.onTriggered(status);
        }
    }
}
