package com.toggle.katana2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class Font {
    private final float fontHeight, fontAscent, fontDescent;
    private final Texture texture;
    private final GLRenderer renderer;

    private static class Char {
        public float width;
        public float u, v, w, h;
    }

    private final static int NUM_CHARS = 128;

    private final Char[] mChars = new Char[NUM_CHARS];

    public Font(GLRenderer renderer, Typeface typeface, float textSize) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setColor(Color.WHITE);
        paint.setTypeface(typeface);

        Paint.FontMetrics fm = paint.getFontMetrics();
        fontHeight = (float)Math.ceil(Math.abs(fm.bottom) + Math.abs(fm.top));
        fontAscent = (float)Math.ceil(Math.abs(fm.ascent));
        fontDescent = (float)Math.ceil(Math.abs(fm.descent));

        float[] w = new float[2];
        float maxCharWidth = 0;

        for (int i=0; i<NUM_CHARS; ++i) {
            mChars[i] = new Char();
            paint.getTextWidths(Character.toString((char)i), w);
            mChars[i].width = w[0];
            if (w[0] > maxCharWidth)
                maxCharWidth = w[0];
        }

        int cellWidth = (int)maxCharWidth;
        int cellHeight = (int)fontHeight;
        int maxSize = cellWidth > cellHeight ? cellWidth : cellHeight;

        int textureSize;
        if (maxSize <= 20)
            textureSize = 256;
        else if (maxSize <= 40)
            textureSize = 512;
        else if (maxSize <= 80)
            textureSize = 1024;
        else
            textureSize = 2048;

        Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0x00000000);

        /*int colCnt = textureSize/cellWidth;
        int rowCnt = (int)Math.ceil((float)NUM_CHARS/(float)colCnt);*/

        float x = 0;
        float y = (cellHeight-1) - fontDescent;
        float cy = 0;
        for (int i=0; i<NUM_CHARS; ++i) {
            Char c = mChars[i];
            c.u = x / textureSize;
            c.v = cy / textureSize;
            c.w = (float)cellWidth / textureSize;
            c.h = (float)cellHeight / textureSize;

            canvas.drawText(Character.toString((char)i), x, y, paint);

            x += cellWidth;
            if (x + cellWidth > textureSize) {
                x = 0;
                y += cellHeight;
                cy += cellHeight;
            }
        }

        texture = renderer.addTexture(bitmap, cellWidth, cellHeight);
        texture.originX = 0;
        texture.originY = 0;
        this.renderer = renderer;
    }

    public float spacing = 2;

    public void setColor(float[] color) {
        texture.color = color;
    }

    public void draw(String text, float x, float y, float angle, float scaleX, float scaleY) {
        float dx = x, dy = y + fontHeight/2;
        for (int i=0; i<text.length(); ++i) {
            char j = text.charAt(i);
            if (j == '\n') {
                dx = x;
                dy += fontHeight;
            }
            else {
                Char c = mChars[(int) j];
                texture.draw(renderer, dx, dy, 0, angle, scaleX, scaleY, c.u, c.v, c.w, c.h);
                dx += c.width + spacing;
            }
        }
    }
}
