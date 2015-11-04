package com.toggle.flipped;

import com.toggle.katana2d.Component;

public class Enemy implements Component {
    // Enemy internal states
    // Need to update Bot states accordingly
    // E.g. when in chase state, set to Moving state with appropriate direction

    enum State { IDLE, CHASE, ATTACK };
    State state = State.IDLE;
}
