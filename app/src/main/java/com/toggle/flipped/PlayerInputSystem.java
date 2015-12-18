package com.toggle.flipped;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.Camera;
import com.toggle.katana2d.Game;
import com.toggle.katana2d.TouchInputData;
import com.toggle.katana2d.Entity;
import com.toggle.katana2d.Transformation;
import com.toggle.katana2d.physics.PhysicsSystem;

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

    private float mMaxWidth, mMaxHeight;

    public PlayerInputSystem(Game game, float maxWidth, float maxHeight) {
        super(new Class[] { Player.class, Bot.class, Transformation.class});
        mGame = game;
        mMaxHeight = maxHeight; mMaxWidth = maxWidth;
    }

    @Override
    public void update(float dt) {
        TouchInputData touchData = mGame.getTouchInputData();
        Boundary motionControlLimit, actionControlLimit;
        motionControlLimit = new Boundary();
        actionControlLimit = new Boundary();

        float devWidth = mGame.getRenderer().width;
        float devHeight = mGame.getRenderer().height;

        motionControlLimit.left = 0.0f;
        motionControlLimit.right = 2.0f * devWidth / 5.0f;
        motionControlLimit.top = 0.0f;
        motionControlLimit.bottom = devHeight;

        actionControlLimit.left = 3.0f * devWidth / 5.0f;
        actionControlLimit.right = devWidth;
        actionControlLimit.top = 0.0f;
        actionControlLimit.bottom = devHeight;


        for (Entity e : mEntities) {
            //Player p = e.get(Player.class);
            Bot b = e.get(Bot.class);
            if (!b.disableInput) {
                if (b.cameraPos == null) {
                    boolean hanging = b.actionState == Bot.ActionState.HANG || b.actionState == Bot.ActionState.HANG_UP;
                    boolean idleState = b.actionState == Bot.ActionState.PICK || b.actionState == Bot.ActionState.FADE_IN || b.actionState == Bot.ActionState.FADE_OUT || b.actionState == Bot.ActionState.FADE_COMPLETE;

                    int dxLimit = 3;
                    int dyLimit = 3;
                    for (int i = 0; i < touchData.pointers.size(); i++) {
                        TouchInputData.Pointer touch;
                        try {
                            touch = touchData.pointers.valueAt(i);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            continue;
                        }
                        if (motionControlLimit.hitTest(touch.x, touch.y) && !idleState) {
                            b.touchX = Math.max(b.touchX, Math.abs(touch.vx / 30.0f));
                            if (touch.dx > dxLimit
                                    && !(hanging && b.direction == Bot.Direction.RIGHT)) {
                                b.direction = Bot.Direction.RIGHT;
                                b.motionState = Bot.MotionState.MOVE;
                            } else if (touch.dx < -dxLimit
                                    && !(hanging && b.direction == Bot.Direction.LEFT)) {
                                b.direction = Bot.Direction.LEFT;
                                b.motionState = Bot.MotionState.MOVE;
                            }
                        } else if (actionControlLimit.hitTest(touch.x, touch.y)) {
                            // if vertical sliding direction is up, and sliding magnitude is big enough
                            // then jump (or move up if we are hanging)

                            boolean flipped = false;//mGame.getRenderer().getCamera().angle == 180;
                            float dy = touch.vy / 10.0f;
                            boolean slideUp = false;
                            if (/*!flipped &&*/ dy < -dyLimit) {
                                slideUp = true;
                                dy = -dy;
                            }/* else if (flipped && dy > dyLimit) {
                        slideUp = true;
                    }*/
                            if (slideUp) {
                                b.touchY = dy;
                                if (b.actionState == Bot.ActionState.HANG)
                                    b.actionState = Bot.ActionState.HANG_UP;
                                else if (b.actionState == Bot.ActionState.NOTHING)
                                    b.actionState = Bot.ActionState.JUMP_START;
                            }

                            // if movement is small, we just tapped for action
                            else if (!b.actionStart && Math.abs(touch.dx) < 4 && Math.abs(touch.dy) < 4
                                    && b.actionState != Bot.ActionState.JUMP && b.actionState != Bot.ActionState.JUMP_START) {
                                b.actionStart = true;
                                b.touchX = touch.x;
                                b.touchY = touch.y;
                            }

                        }

                    }

                    if (touchData.pointers.size() == 0 || idleState) {
                        b.motionState = Bot.MotionState.IDLE;
                        b.touchX = 0;
                    }
                } else {
                    b.motionState = Bot.MotionState.IDLE;
                    b.touchX = b.touchY = 0;
                }
            }

            // We may do this in a different system, but for now
            // set the camera position according to the player position.
            Transformation t = e.get(Transformation.class);
            /*float w = mGame.getRenderer().width;
            float h = mGame.getRenderer().height;*/

            if (t.y > mMaxHeight+48)
                b.dead = true;


            // Scroll the camera so that player is at the center of screen.
            // Make sure the camera don't go off the edges of the world,
            // assuming the world is twice the width of camera view (w*2).
            // This assumption is temporary.

            float x = t.x, y = t.y;
            if (b.cameraPos == null && camLastPos == null) {
                cameraMoving = false;
                if (b.actionState == Bot.ActionState.HANG || b.actionState == Bot.ActionState.HANG_UP) {
                    Vector2 vv = new Vector2(b.climbPositions.get(b.climbPositions.size() - 1)).scl(PhysicsSystem.PIXELS_PER_METER);
                    Transformation tt = ((Entity) b.hanger.getUserData()).get(Transformation.class);
                    if (b.direction == Bot.Direction.LEFT) {
                        x = tt.x + vv.x;
                        y = tt.y + vv.y;
                    } else if (b.direction == Bot.Direction.RIGHT) {
                        x = tt.x - vv.x;
                        y = tt.y + vv.y;
                    }
                }
            }
            else {
                if (b.cameraPos == null && camLastPos != null) {
                    if (!camReverse) {
                        camReverse = true;
                        camT = CAM_MOVE_TIME;
                    }
                } else
                    camLastPos = b.cameraPos;

                if (!cameraMoving)
                    camT = 0;
                if (camT <= 0 && camReverse) {
                    camReverse = false;
                    cameraMoving = false;

                    b.cameraPos = null;
                    b.cameraFocusLimit = false;
                    camLastPos = null;
                }
                else if (!camReverse && camT >= CAM_MOVE_TIME) {
                    x = camLastPos.x;
                    y = camLastPos.y;
                    if (b.cameraFocusLimit) {
                        if (b.cameraFocusTime > 0)
                            b.cameraFocusTime -= dt;
                        else {
                            camReverse = true;
                            camT = CAM_MOVE_TIME;
                        }
                    }
                } else {
                    cameraMoving = true;
                    if (camReverse)
                        camT -= dt;
                    else
                        camT += dt;
                    float tt = Math.max(0, (CAM_MOVE_TIME - camT)) /  CAM_MOVE_TIME;
                    float ttMinus = 1 - tt;
                    x  = x * tt + camLastPos.x * ttMinus;
                    y  = y * tt + camLastPos.y * ttMinus;
                }
            }

            mGame.getRenderer().centerCamera(x, y, mMaxWidth, mMaxHeight);
        }
    }

    private boolean cameraMoving = false, camReverse = false;
    private float camT = 0;
    private Vector2 camLastPos;

    private static final float CAM_MOVE_TIME = 0.8f;
}
