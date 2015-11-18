package com.toggle.katana2d;

// Uses sprite and transformation components of entities to render them
public class RenderSystem extends System {
    private GLRenderer mRenderer;
    public RenderSystem(GLRenderer renderer) {
        super(new Class[]{Sprite.class, Transformation.class});
        mRenderer = renderer;
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

            float minus = 1-interpolation;
            float x = t.x * interpolation + t.lastX * minus;
            float y = t.y * interpolation + t.lastY * minus;
            float angle = t.angle * interpolation + t.lastAngle * minus;

            s.draw(mRenderer, x, y, angle);
        }
    }
}
