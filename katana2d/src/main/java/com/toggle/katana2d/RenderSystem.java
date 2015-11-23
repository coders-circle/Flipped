package com.toggle.katana2d;

import java.util.Collections;

// Uses sprite and transformation components of entities to render them
public class RenderSystem extends System {
    private GLRenderer mRenderer;
    public RenderSystem(GLRenderer renderer) {
        super(new Class[]{Sprite.class, Transformation.class});
        mRenderer = renderer;
    }

    @Override
    public void onEntityAdded(Entity entity) {
        // sort by distance
        float d = entity.get(Sprite.class).distance;
        int i = mEntities.size() - 2;
        int j = mEntities.size() - 1;
        if (i >= 0)
        while (i >= 0) {
            if (mEntities.get(i).get(Sprite.class).distance < d) {
                Collections.swap(mEntities, i, j);
                j = i;
            }
            else
                break;
            i--;
        }
    }

    @Override
    public void update(float dt) {
        for (Entity entity : mEntities) {
            Sprite sc = entity.get(Sprite.class);
            sc.animate(dt);
        }
    }

    @Override
    public void draw(float interpolation) {
        for (Entity entity : mEntities) {
            Sprite s = entity.get(Sprite.class);
            Transformation t = entity.get(Transformation.class);

            // Interpolation to fix temporal aliasing
            float minus = 1-interpolation;
            float x = t.x * interpolation + t.lastX * minus;
            float y = t.y * interpolation + t.lastY * minus;
            float angle = t.angle * interpolation + t.lastAngle * minus;

            s.draw(mRenderer, x, y, angle);
        }
    }
}
