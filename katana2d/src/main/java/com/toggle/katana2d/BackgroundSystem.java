package com.toggle.katana2d;

import java.util.Collections;

public class BackgroundSystem extends System {
    public static final float MAX_Z = 100;

    private GLRenderer mRenderer;
    public BackgroundSystem(GLRenderer renderer) {
        super(new Class[]{Background.class});
        mRenderer = renderer;
    }

    @Override
    public void onEntityAdded(Entity entity) {
        // sort by distance
        float d = entity.get(Background.class).distance;
        int i = mEntities.size() - 2;
        int j = mEntities.size() - 1;
        if (i >= 0)
            while (i >= 0) {
                if (mEntities.get(i).get(Background.class).distance < d) {
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
        for (Entity entity: mEntities) {
            Background b = entity.get(Background.class);

            b.x = b.offsetX + mRenderer.getCamera().x * b.distance;
            b.y = b.offsetY + mRenderer.getCamera().y * b.distance;
        }
    }

    @Override
    public void draw() {
        for (Entity entity: mEntities) {
            Background b = entity.get(Background.class);
            b.mTexture.draw(mRenderer, b.x, b.y, -b.distance, 0, 1, 1);
        }
    }
}
