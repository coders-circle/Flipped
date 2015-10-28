package com.toggle.flipped;

import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.TouchInputData;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Transformation;

public class PlayerInputSystem extends com.toggle.katana2d.System {

    private Game mGame; // Is used to take input data like current touch input;

    public PlayerInputSystem(Game game) {
        super(new Class[] { Player.class, Bot.class, Transformation.class});
        mGame = game;
    }

    @Override
    public void update(double dt) {
        TouchInputData touch = mGame.getTouchInputData();

        for (Entity e: mEntities) {
            Player p = e.get(Player.class);
            Bot b = e.get(Bot.class);

            if (touch.isTouchDown) {
                // when user is touching on the screen,
                // set move-direction to direction of dx
                if (touch.dx > 5) {
                    b.direction = Bot.Direction.RIGHT;
                    b.motionState = Bot.MotionState.MOVE;
                }
                else if (touch.dx < -5) {
                    b.direction = Bot.Direction.LEFT;
                    b.motionState = Bot.MotionState.MOVE;
                }

                // if vertical sliding direction is up, and sliding magnitude is more than 8
                // then jump
                if (touch.dy < -8)
                    b.actionState = Bot.ActionState.JUMP_START;
            }
            else
                // We are not touching on screen means, there's no need to move the character
                b.motionState = Bot.MotionState.IDLE;


            // We may do this in a different system, but for now
            // set the camera position according to the player position.
            Transformation t = e.get(Transformation.class);

            // Scroll the camera so that player is at the center of screen.
            // Make sure the camera don't go off the edges of the world,
            // assuming the world is twice the width of camera view (w*2).
            // This assumption is temporary.
            Camera camera = mGame.getRenderer().getCamera();
            float w = mGame.getRenderer().width;
            float h = mGame.getRenderer().height;
            camera.x = Math.min(Math.max(w/2, t.x), w*2-w/2) - w/2;
            camera.y = Math.min(h/2, t.y) - h/2;
        }
    }

}
