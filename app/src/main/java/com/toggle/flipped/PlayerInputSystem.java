package com.toggle.flipped;

import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.TouchInputData;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Transformation;

public class PlayerInputSystem extends com.toggle.katana2d.System {

    private Game mGame; // Is used to take input data like current touch input;

    private class Boundary{
        Boundary(){
            left = right = top = bottom = 0.0f;
        }
        public float left, right, top, bottom;
        public boolean hitTest(float x, float y){
            return ((x >= left && x <= right) && (y >= top && y <= bottom));
        }
    }

    public PlayerInputSystem(Game game) {
        super(new Class[] { Player.class, Bot.class, Transformation.class});
        mGame = game;
    }

    @Override
    public void update(float dt) {
        TouchInputData touchData = mGame.getTouchInputData();

        Boundary motionControlLimit, actionControlLimit;
        motionControlLimit = new Boundary();
        actionControlLimit = new Boundary();

        float devWidth = mGame.getRenderer().devWidth;
        float devHeight = mGame.getRenderer().devHeight;
        // TODO: following values are constant for the given screen size, move to proper place may be
        // vertical limit = lower half of the screen
        // motion control =  left side, action = right
        motionControlLimit.left = 0.0f;
        motionControlLimit.right = 2.0f*devWidth/5.0f;
        motionControlLimit.top = devHeight/2.0f;
        motionControlLimit.bottom = devHeight;

        actionControlLimit.left = 3.0f*devWidth/5.0f;
        actionControlLimit.right = devWidth;
        actionControlLimit.top  = devHeight/2.0f;
        actionControlLimit.bottom = devHeight;


        for (Entity e: mEntities) {
            Player p = e.get(Player.class);
            Bot b = e.get(Bot.class);

            for(int i = 0; i < touchData.pointers.size(); i++){
                TouchInputData.Pointer touch = touchData.pointers.valueAt(i);
                if(motionControlLimit.hitTest(touch.x, touch.y)){
                    if(touch.dx > 5){
                        b.direction = Bot.Direction.RIGHT;
                        b.motionState = Bot.MotionState.MOVE;
                    }
                    else if (touch.dx < -5) {
                        b.direction = Bot.Direction.LEFT;
                        b.motionState = Bot.MotionState.MOVE;
                    }
                }
                else if(actionControlLimit.hitTest(touch.x, touch.y)){
                    if (touch.dy < -8 )
                        b.actionState = Bot.ActionState.JUMP_START;
                }
            }

            if(touchData.pointers.size() == 0){
                b.motionState = Bot.MotionState.IDLE;
            }


            /* ========================= old logic ===========================

            // If at least one pointer is touching
            if (touchData.pointers.size() > 0) {
                // User first pointer to process
                TouchInputData.Pointer touch = touchData.pointers.valueAt(0);

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

                // if vertical sliding direction is up, and sliding magnitude is big enough
                // then jump
                if (touch.dy < -8 )
                    b.actionState = Bot.ActionState.JUMP_START;
            }

            else
                // We are not touching on screen means, there's no need to move the character
                b.motionState = Bot.MotionState.IDLE;
            //*/
            //====================== end of old logic =========================

            // We may do this in a different system, but for now
            // set the camera position according to the player position.
            Transformation t = e.get(Transformation.class);
            float w = mGame.getRenderer().width;
            float h = mGame.getRenderer().height;


            // Scroll the camera so that player is at the center of screen.
            // Make sure the camera don't go off the edges of the world,
            // assuming the world is twice the width of camera view (w*2).
            // This assumption is temporary.
            Camera camera = mGame.getRenderer().getCamera();
            float maxW = 7520, maxH = 640-140;
            camera.x = Math.min(Math.max(w/2, t.x), maxW-w/2) - w/2;
            camera.y = Math.min(Math.max(h/2, t.y), maxH-h/2) - h/2;
        }
    }

}
