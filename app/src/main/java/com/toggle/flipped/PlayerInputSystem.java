package com.toggle.flipped;

import com.toggle.katana2d.Game;
import com.toggle.katana2d.TouchInputData;
import com.toggle.katana2d.Entity;

public class PlayerInputSystem extends com.toggle.katana2d.System {

    private Game mGame; // Is used to take input data like current touch input;

    public PlayerInputSystem(Game game) {
        super(new Class[] { Player.class, Bot.class });
        mGame = game;
    }

    @Override
    public void update(double dt) {
        TouchInputData touch = mGame.getTouchInputData();

        for (Entity e: mEntities) {
            Player p = e.get(Player.class);
            Bot b = e.get(Bot.class);

            // if (touch.isTouchDown) { ... }
            // else { ... }

        }
    }

}
