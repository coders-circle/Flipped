package com.toggle.katana2d;

public class RenderSystem extends System {
    public RenderSystem() {
        super(new Class[]{Sprite.class, Transformation.class});
    }

    @Override
    public void update(double dt) {
        for (Entity entity : mEntities) {
            Sprite sc = entity.getComponent(Sprite.class);
            Sprite.SpriteSheetData ssd = sc.spriteSheetData;

            // animate a sprite sheet by advancing the image index when required time has elapsed
            if (ssd != null && ssd.animationSpeed > 0) {
                ssd.timePassed += (float) dt;
                if (ssd.timePassed >= 1.0/ssd.animationSpeed) {
                    ssd.timePassed = 0;
                    ssd.index = (ssd.index+1)%(ssd.numCols*ssd.numRows);
                }
            }
        }
    }

    @Override
    public void draw() {
        for (Entity entity : mEntities) {
            Sprite sc = entity.getComponent(Sprite.class);
            Transformation tc = entity.getComponent(Transformation.class);

            if (sc.glSprite == null)
                continue;;

            if (sc.spriteSheetData == null)
                sc.glSprite.draw(tc.x, tc.y, tc.angle);
            else {
                Sprite.SpriteSheetData ssd = sc.spriteSheetData;

                int col = ssd.index % ssd.numCols;
                int row = ssd.index / ssd.numCols;

                float clipX = (ssd.imgWidth + ssd.hSpacing) * col + ssd.offsetX;
                float clipY = (ssd.imgHeight + ssd.vShacing) * row + ssd.offsetY;

                clipX /= sc.glSprite.mTexture.width;
                clipY /= sc.glSprite.mTexture.height;

                float clipW = ssd.imgWidth / sc.glSprite.mTexture.width;
                float clipH = ssd.imgHeight / sc.glSprite.mTexture.height;

                sc.glSprite.draw(tc.x, tc.y, tc.angle, clipX, clipY, clipW, clipH);
            }
        }
    }
}
