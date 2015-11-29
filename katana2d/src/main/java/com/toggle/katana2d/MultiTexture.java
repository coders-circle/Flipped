package com.toggle.katana2d;

import java.util.List;

public class MultiTexture extends Texture {

    public List<Integer> textureIds;
    public final int numCols, numRows;

    public MultiTexture(List<Integer> textureIds, float width, float height, int numCols, int numRows) {
        this.textureIds = textureIds;
        this.width = width;
        this.height = height;
        this.numCols = numCols;
        this.numRows = numRows;
    }

    public void draw(GLRenderer renderer, float x, float y, float z, float angle, float scaleX, float scaleY, float clipX, float clipY, float clipW, float clipH) {

        for (int i=0; i<numRows; ++i)
        for (int j=0; j<numCols; ++j) {
            int textureId = textureIds.get(j+i*numCols);
            float xx = x + width * j;
            float yy = y + height * i;
            draw(textureId, renderer, xx, yy, z, angle, scaleX, scaleY, clipX, clipY, clipW, clipH);
        }
    }
}
