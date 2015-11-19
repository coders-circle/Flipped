package com.toggle.katana2d;

import android.content.Context;
import android.util.Log;

import com.toggle.katana2d.physics.PhysicsSystem;

import org.jbox2d.common.Vec2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    public static int getResourceId(Context context, String resourceType, String resourceName)
    {
        try {
            return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Read text from a raw resource file
    public static String getRawFileText(Context context, int rawResId) {
        InputStream inputStream  = context.getResources().openRawResource(rawResId);
        String s = new java.util.Scanner(inputStream).useDelimiter("\\A").next();

        try {
            inputStream.close();
        } catch (IOException e) {
            Log.e("Raw File read Error", e.getMessage());
        }
        return s;
    }

    public static List<Vec2> parsePoints(String string, float offsetX, float offsetY) {
        // parse the points and create vertices, also convert from pixels to meters
        // TODO: This needs to be verified

        // Regex matching to get every x, y
        Pattern pattern = Pattern.compile("(\\-?\\d+\\.?\\d*),?\\s*(\\-?\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(string);

        List<Vec2> vertices = new ArrayList<>();
        while (matcher.find()) {
            float x = Float.parseFloat(matcher.group(1)) * PhysicsSystem.METERS_PER_PIXEL - offsetX;
            float y = Float.parseFloat(matcher.group(2)) * PhysicsSystem.METERS_PER_PIXEL - offsetY;
            vertices.add(new Vec2(x, y));
        }
        return vertices;
    }

    public static void scale(List<Vec2> points, float scaleX, float scaleY) {
        for (Vec2 p: points) {
            p.x *= scaleX;
            p.y *= scaleY;
        }
    }
}
