package com.toggle.katana2d;

public class BackgroundSystem extends System {

    private GLRenderer mRenderer;
    private float mMaxZ;
    public BackgroundSystem(GLRenderer renderer, float maxZ) {
        super(new Class[]{Background.class});
        mRenderer = renderer;
        mMaxZ = maxZ;
    }

    @Override
    public void update(float dt) {
        for (Entity entity: mEntities) {
            Background b = entity.get(Background.class);

            b.lastX = b.x;
            b.x = mRenderer.getCamera().x * (1 - (mMaxZ - b.z) / mMaxZ);
        }
    }

    @Override
    public void draw(float interpolation) {
        for (Entity entity: mEntities) {
            Background b = entity.get(Background.class);

            float x = b.x * interpolation + b.lastX * (1-interpolation);
            b.mTexture.draw(mRenderer, x, 0, -b.z, 0, 1, 1);
        }
    }
}
