package com.toggle.flipped;

import com.toggle.katana2d.*;

public class HelpSystem extends com.toggle.katana2d.System {
    private GLRenderer mRenderer;

    public HelpSystem (GLRenderer renderer) {
        super(new Class[]{Bot.class});
        mRenderer = renderer;
    }

    enum Arrow { None, Left, Right, Up };
    private Arrow current = Arrow.None;
    private float time = 0;
    private static boolean done = false;

    private Sprite left, right, up, dotted;

    @Override
    public void init() {
        left = new Sprite(mRenderer.addTexture(R.drawable.left, 100, 45), -10, 8, 1);
        right = new Sprite(mRenderer.addTexture(R.drawable.right, 100, 45), -10, 8, 1);
        up = new Sprite(mRenderer.addTexture(R.drawable.up, 45, 100), -10, 8, 1);
        dotted = new Sprite(mRenderer.addTexture(R.drawable.dotted_line, new float[]{1,1,1,0.2f}, 8, 320), -10);
    }

    @Override
    public void onEntityAdded(Entity e) {
        if (done)
            return;
        Bot b = e.get(Bot.class);
        b.disableInput = true;
    }

    private static final float ARROW_TIME = 2;
    @Override
    public void update(float dt) {
        if (done)
            return;
        for (Entity e: mEntities) {
            Bot b = e.get(Bot.class);

            switch (current) {
                case None:
                    b.actionState = Bot.ActionState.NOTHING;
                    b.motionState = Bot.MotionState.IDLE;
                    break;
                case Left:
                    b.actionState = Bot.ActionState.NOTHING;
                    b.motionState = Bot.MotionState.MOVE;
                    b.direction = Bot.Direction.LEFT;
                    b.touchX = 5;
                    left.animate(dt);
                    break;
                case Right:
                    b.actionState = Bot.ActionState.NOTHING;
                    b.motionState = Bot.MotionState.MOVE;
                    b.direction = Bot.Direction.RIGHT;
                    b.touchX = 5;
                    right.animate(dt);
                    break;
                case Up:
                    if (b.actionState != Bot.ActionState.JUMP && b.actionState != Bot.ActionState.JUMP_START)
                    b.actionState = Bot.ActionState.JUMP_START;
                    b.touchX = 0;
                    b.touchY = 5;
                    up.animate(dt);
                    break;
            }

            time += dt;
            if (time > ARROW_TIME) {
                time = 0;
                switch (current) {
                    case None:
                        current = Arrow.Left;
                        break;
                    case Left:
                        current = Arrow.Right;
                        break;
                    case Right:
                        current = Arrow.Up;
                        break;
                    case Up:
                        b.disableInput = false;
                        current = Arrow.None;
                        done = true;
                }
            }
        }
    }

    @Override
    public void draw() {
        if (done)
            return;

        float cx = mRenderer.getCamera().x, cy = mRenderer.getCamera().y;
        float w = mRenderer.width, h = mRenderer.height;

        dotted.draw(mRenderer, cx+w/2, cy+h/2, 0);
        switch (current) {
            case Left:
                left.draw(mRenderer, cx + w/4f,  cy + h*3f/4f, 0);
                break;
            case Right:
                right.draw(mRenderer, cx + w/4f,  cy + h*3f/4f, 0);
                break;
            case Up:
                up.draw(mRenderer, cx + w*3.3f/4f,  cy + h*3f/4f, 0);
                break;
        }
    }
}
