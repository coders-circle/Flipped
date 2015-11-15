package com.toggle.katana2d;


// Uses sprite and transformation components of entities to render them
public class RenderSystem extends System {
    public RenderSystem() {
        super(new Class[]{Sprite.class, Transformation.class});
    }

    @Override
    public void update(double dt) {
        for (Entity entity : mEntities) {
            Sprite sc = entity.get(Sprite.class);
            sc.animate(dt);
        }
    }

    @Override
    public void draw() {
        for (Entity entity : mEntities) {
            Sprite sc = entity.get(Sprite.class);
            Transformation tc = entity.get(Transformation.class);

            sc.draw(tc.x, tc.y, tc.angle);
        }
    }
}
