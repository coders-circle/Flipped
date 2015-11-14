package com.toggle.katana2d;

// Sprite component that stores sprite-sheet data and a reference to GLSprite
public class Sprite implements Component{
    public GLSprite glSprite;
    public SpriteSheetData spriteSheetData;
    public boolean isReflected = false;

    public Sprite(GLSprite glSprite) {
        this.glSprite = glSprite;
    }

    public Sprite(GLSprite glSprite, SpriteSheetData spriteSheetData) {
        this.glSprite = glSprite;
        this.spriteSheetData = spriteSheetData;
    }

    public static class SpriteSheetData {
        public float offsetX = 0, offsetY = 0, imgWidth, imgHeight, hSpacing = 0, vShacing = 0;
        public int numRows = 1, numCols = 1;
        public int numImages = -1;              // If -1 then numImages = numRows x numCols

        public float animationSpeed = 12; // in FPS
        public float timePassed = 0;     // time that has elapsed since last frame

        public int index = 0;	// the index of image to draw next
    }

    public void changeSprite(GLSprite sprite, SpriteSheetData newSpriteSheetData) {
        if (sprite != null)
            glSprite = sprite;

        if (spriteSheetData == newSpriteSheetData || newSpriteSheetData == null)
            return;

        if (spriteSheetData != null && spriteSheetData.animationSpeed > 0) {
            spriteSheetData.index = 0;
            spriteSheetData.timePassed = 0;
        }
        spriteSheetData = newSpriteSheetData;
    }


    public void draw(float x, float y, float angle) {
        if (glSprite == null)
            return;

        if (isReflected) {
            x += glSprite.width;
            glSprite.width *= -1;
        }

        if (spriteSheetData == null)
            glSprite.draw(x, y, angle);
        else {
            Sprite.SpriteSheetData ssd = spriteSheetData;

            int col = ssd.index % ssd.numCols;
            int row = ssd.index / ssd.numCols;

            float clipX = (ssd.imgWidth + ssd.hSpacing) * col + ssd.offsetX;
            float clipY = (ssd.imgHeight + ssd.vShacing) * row + ssd.offsetY;

            clipX /= glSprite.mTexture.width;
            clipY /= glSprite.mTexture.height;

            float clipW = ssd.imgWidth / glSprite.mTexture.width;
            float clipH = ssd.imgHeight / glSprite.mTexture.height;

            glSprite.draw(x, y, angle, clipX, clipY, clipW, clipH);
        }

        if (isReflected)
            glSprite.width *= -1;
    }
}
