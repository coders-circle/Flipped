package com.toggle.katana2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private List<Entity> postDrawingList = new ArrayList<>();
    @Override
    public void draw() {
        postDrawingList.clear();
        for (Entity entity : mEntities) {
            Sprite s = entity.get(Sprite.class);
            Transformation t = entity.get(Transformation.class);

            if (s.postDrawn)
                postDrawingList.add(entity);
            else
                s.draw(mRenderer, t.x, t.y, t.angle);
        }
    }

    @Override
    public void postDraw() {
        for (Entity entity: postDrawingList) {
            Sprite s = entity.get(Sprite.class);
            Transformation t = entity.get(Transformation.class);
            s.draw(mRenderer, t.x, t.y, t.angle);
        }
    }
}
