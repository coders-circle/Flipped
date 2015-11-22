package com.toggle.flipped;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.toggle.katana2d.Component;
import com.toggle.katana2d.Sprite;

/*import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Body;*/

import java.util.ArrayList;
import java.util.List;

// A Rope contains series of segments, each joined using a RevoluteJoint.
// The first segment is joined to some other body (startBody)
// and the last segment can be optionally joined to endBody (can be set to null).
public class Rope implements Component {

    public static final float STANDARD_SEGMENT_LENGTH = 5f;
    public static final float STANDARD_SEGMENT_THICKNESS = 4f;
    Sprite segmentSprite;

    // Create a rope along given path using small rope segments of
    // given length and thickness
    public Rope(List<Vector2> path, float thickness, float segmentLength, Body startBody,
                Body endBody) {this.thickness = thickness;
        this.segmentLength = segmentLength;
        this.startBody = startBody;
        this.endBody = endBody;
        this.path = path;
    }

    final Body startBody;
    final Body endBody;
    final List<Vector2> path;    // path for the rope segment

    // Segments data
    int numSegments;
    final float thickness;
    final float segmentLength;

    // List of box2d bodies representing each segment
    final List<Body> segments = new ArrayList<>();

    public void removeSegment(int i) {
        Body body = segments.get(i);
        body.getWorld().destroyBody(body);
        segments.remove(body);
    }

    // Burn data for burning the rope
    public static class BurnData {
        float timeToBurn;       // time to burn a segment
        float timePassed = 0;   // time passed since burning current segment started

        Sprite burningSegmentSprite;
    }

    public boolean isBurning = false;
    public BurnData burnData;

    public BurnData makeBurnable() {
        burnData = new BurnData();
        return burnData;
    }

    public void startBurning() {
        if (burnData == null)
            makeBurnable();
        isBurning = true;
    }
}
