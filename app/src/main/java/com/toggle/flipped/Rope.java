package com.toggle.flipped;

import com.toggle.katana2d.Component;
import com.toggle.katana2d.Sprite;

import org.jbox2d.dynamics.Body;

import java.util.ArrayList;
import java.util.List;

// A Rope contains series of segments, each joined using a RevoluteJoint.
// The first segment is joined to some other body (startBody)
// and the last segment can be optionally joined to endBody (can be set to null).
public class Rope implements Component {

    Sprite segmentSprite;

    public Rope(float x, float y, int numberOfSegments, float thickness, float segmentLength,
                Body startBody, Body endBody) {
        numSegments = numberOfSegments;
        this.thickness = thickness;
        this.segmentLength = segmentLength;

        this.initX = x; this.initY = y;
        this.startBody = startBody;
        this.endBody = endBody;
    }

    final Body startBody;
    final Body endBody;

    final float initX, initY;       // initial position for the first segment.

    // Segments data
    final int numSegments;
    final float thickness;
    final float segmentLength;

    // List of box2d bodies representing each segment
    final List<Body> segments = new ArrayList<>();

    // To remove a segment, add the box2d body to this list
    final List<Body> segmentsToDelete = new ArrayList<>();


    // Burn data for burning the rope
    // TODO
    private static class BurnData {
        float timeToBurn;       // time to burn a segment
        float timePassed = 0;   // time passed since burning current segment started

        Sprite burningSegmentSprite;
    }
}
