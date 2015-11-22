package com.toggle.katana2d;

import android.content.Context;
import android.util.Log;

import com.toggle.katana2d.physics.PhysicsSystem;

//import org.jbox2d.common.Vector2;
import com.badlogic.gdx.math.Vector2;

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

}
