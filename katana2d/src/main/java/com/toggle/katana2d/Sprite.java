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

        public float animationSpeed = 12; // in FPS
        public float timePassed = 0;     // time that has elapsed since last frame

        public int index = 0;	// the index of image to draw next
    }

    public void changeSpriteSheet(SpriteSheetData newSpriteSheetData) {
        if (spriteSheetData == newSpriteSheetData)
            return;

        if (spriteSheetData != null) {
            spriteSheetData.index = 0;
            spriteSheetData.timePassed = 0;
        }
        spriteSheetData = newSpriteSheetData;
    }
}
