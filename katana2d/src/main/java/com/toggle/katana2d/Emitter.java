package com.toggle.katana2d;

// An emitter that emits particles
public class Emitter implements Component {
    public final int maxParticles;  // Total number of particles
    public final int textureId;   // Texture to use for drawing each particle

    // Each particle has its own postion, speed, acceleration, size, color and life
    public static class Particle {
        float x, y, speed_x, speed_y, accel_x, accel_y, size;
        float[] startColor = new float[4], rangeColor = new float[4]; // range = end - start
        float life;
    }

    // Create a new emitter
    public Emitter(GLRenderer renderer, int maxParticles, int textureId) {
        this.maxParticles = maxParticles;
        this.textureId = textureId;
        pointSpritesData = new float[PointSprites.ELEMENTS_PER_POINT * maxParticles];
        pointSprites = new PointSprites(renderer, textureId, maxParticles);
        particles = new Particle[maxParticles];
    }

    // Create a new emitter
    public Emitter(GLRenderer renderer, int maxParticles, int textureId,
                   float life, float emissionRate, float[] startColor, float[] endColor) {
        this.maxParticles = maxParticles;
        this.textureId = textureId;
        this.life = life;
        this.emissionRate = emissionRate;
        this.startColor = startColor;
        this.endColor = endColor;
        pointSpritesData = new float[PointSprites.ELEMENTS_PER_POINT * maxParticles];
        pointSprites = new PointSprites(renderer, textureId, maxParticles);
        particles = new Particle[maxParticles];
    }

    // Particle emission data:

    public float emissionRate;      // rate to emit particles
    public float life = 1;          // total time each particle can live up to

    public float offsetAngle = 0;

    public float[] startColor = new float[] {1, 0, 0, 1};   // start and end colors of the particle
    public float[] endColor = new float[] {1, 0, 0, 1};
    public float[] var_startColor = new float[] {0, 0, 0, 0};   // vary start and end colors of each particle
    public float[] var_endColor = new float[] {0, 0, 0, 0};     // by these amounts
    public float var_x = 0, var_y = 0, var_angle = 90;      // vary position and angle of each particle upon emission by these amounts
    public float size = 2, var_size = 0;        // size and amount to vary the size for each particle
    public float speed = 5f, var_speed = 0;     // speed and amount to vary the speed for each particle
    public float accel_x = 0, var_accel_x = 0;  // x-axis acceleration and amount to vary it
    public float accel_y = 0, var_accel_y = 0;  // y-axis acceleration and amount to vary it
    public boolean additiveBlend = false;       // blend the particles additively? this makes the area with large number of particles very bright

    public final PointSprites pointSprites;   // Opengl point sprites to draw all particles at once with GPU
    public final float[] pointSpritesData;   // position, color, size of each particle used by GLPointSprites
    public final Particle[] particles;       // all particles
    public int numParticles = 0;             // total number of particles emitted

    public boolean emitOnlyOnce = false;    // Emit only once and then set emitNext to false automatically
    public boolean emitNext = true;         // Set this to false to stop emitting and true to start emitting

    public float emissionTime = 0;          // total time since last emission
}
