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
            Sprite.SpriteSheetData ssd = sc.spriteSheetData;

            // animate a sprite sheet by advancing the image index when required time has elapsed
            if (ssd != null && ssd.animationSpeed > 0) {
                if (ssd.numImages < 0)
                    ssd.numImages = ssd.numRows * ssd.numCols;

                ssd.timePassed += (float) dt;
                if (ssd.timePassed >= 1.0/ssd.animationSpeed) {
                    ssd.timePassed = 0;
                    ssd.index = (ssd.index+1)%(ssd.numImages);
                }
            }
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
