package com.toggle.katana2d;

public class Sprite implements Component{
    public GLSprite glSprite;
    public SpriteSheetData spriteSheetData;

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

        public float animationSpeed = 0; // in FPS
        public float timePassed = 0;     // time that has elapsed since last frame

        public int index = 0;	// the index of image to draw next
    }
}
