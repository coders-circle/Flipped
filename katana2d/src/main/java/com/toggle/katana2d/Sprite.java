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

    public void reset() {
        if (spriteSheetData != null) {
            spriteSheetData.index = 0;
            spriteSheetData.timePassed = 0;
        }
    }

    public void animate(double dt) {
        SpriteSheetData ssd = spriteSheetData;
        // animate a sprite sheet by advancing the image index when required time has elapsed
        if (ssd != null && ssd.animationSpeed > 0) {
            if (ssd.numImages < 0)
                ssd.numImages = ssd.numRows * ssd.numCols;

            ssd.timePassed += (float) dt;
            if (ssd.timePassed >= 1.0/ssd.animationSpeed) {
                ssd.timePassed = 0;
                ssd.index++;

                if (ssd.index >= ssd.numImages) {
                    if (ssd.loop)
                        ssd.index = 0;
                    else
                        ssd.index--;
                }
            }
        }
    }

    public static class SpriteSheetData {
        public float offsetX = 0, offsetY = 0, imgWidth, imgHeight, hSpacing = 0, vShacing = 0;
        public int numRows = 1, numCols = 1;
        public int numImages = -1;              // If -1 then numImages = numRows x numCols

        public float animationSpeed = 12; // in FPS
        public boolean loop = true;       // loop animation?
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
