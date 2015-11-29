package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.toggle.katana2d.*;

public class TouchTriggerSystem extends com.toggle.katana2d.System {

    private final Game mGame;

    public TouchTriggerSystem(Game game) {
        super(new Class[]{Trigger.class, Transformation.class});
        mGame = game;
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            Trigger trigger = entity.get(Trigger.class);
            Transformation t = entity.get(Transformation.class);

            TouchInputData inputData = mGame.getTouchInputData();
            Camera camera = mGame.getRenderer().getCamera();
            if (inputData.pointers.size() > 0) {
                for (int i=0; i<inputData.pointers.size(); ++i) {
                    TouchInputData.Pointer p = inputData.pointers.valueAt(i);

                    if (new Vector2(p.x+camera.x-t.x, p.y+camera.y-t.y).len() < 10) {
                        trigger.trigger();
                        break;
                    }
                }
            }
        }
    }
}
