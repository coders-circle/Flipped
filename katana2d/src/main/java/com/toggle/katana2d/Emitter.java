package com.toggle.katana2d;


public class Emitter implements Component {
    public final int maxParticles;
    public final Texture texture;

    public static class Particle {
        float x, y, speed_x, speed_y, accel_x, accel_y, size;
        float[] startColor = new float[4], rangeColor = new float[4]; // range = end - start
        float life;
    }

    public Emitter(GLRenderer renderer, int maxParticles, Texture texture) {
        this.maxParticles = maxParticles;
        this.texture = texture;
        pointSpritesData = new float[GLPointSprites.ELEMENTS_PER_POINT * maxParticles];
        pointSprites = new GLPointSprites(renderer, texture, maxParticles);
        particles = new Particle[maxParticles];
    }

    public Emitter(GLRenderer renderer, int maxParticles, Texture texture,
                   float life, float emissionRate, float[] startColor, float[] endColor) {
        this.maxParticles = maxParticles;
        this.texture = texture;
        this.life = life;
        this.emissionRate = emissionRate;
        this.startColor = startColor;
        this.endColor = endColor;
        pointSpritesData = new float[GLPointSprites.ELEMENTS_PER_POINT * maxParticles];
        pointSprites = new GLPointSprites(renderer, texture, maxParticles);
        particles = new Particle[maxParticles];
    }

    public float emissionRate;
    public float life = 1;
    public float[] startColor = new float[] {1, 0, 0, 1};
    public float[] endColor = new float[] {1, 0, 0, 1};
    public float[] var_startColor = new float[] {0, 0, 0, 0};
    public float[] var_endColor = new float[] {0, 0, 0, 0};
    public float var_x = 0, var_y = 0, var_angle = 90;
    public float size = 2, var_size = 0;
    public float speed = 5f, var_speed = 0;
    public float accel_x = 0, var_accel_x = 0;
    public float accel_y = 0, var_accel_y = 0;
    public boolean additiveBlend = false;

    public final GLPointSprites pointSprites;
    public final float[] pointSpritesData;   // position, color, size of each particle
    public final Particle[] particles;       // particles
    public int numParticles = 0;

    public float emissionTime = 0;
}
