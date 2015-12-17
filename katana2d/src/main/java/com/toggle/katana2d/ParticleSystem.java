package com.toggle.katana2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem extends System {

    private Random mRandom = new Random();

    public ParticleSystem() {
        super(new Class[] {Emitter.class, Transformation.class});
    }

    private float getRandomVar(float var) {
        return mRandom.nextFloat() * var - var/2;
    }

    public void emit(Emitter e, int num) {
        if (e.life == 0)
            return;
        num = Math.min(e.maxParticles - e.numParticles, num);

        for (int i=e.numParticles; i<num+e.numParticles; ++i) {
            float angle = (float)Math.toRadians(getRandomVar(e.var_angle));
            float speed = getRandomVar(e.var_speed) + e.speed;
            Emitter.Particle p = new Emitter.Particle();
            e.particles[i] = p;

            p.x = getRandomVar(e.var_x);
            p.y = getRandomVar(e.var_y);

            p.speed_x = (float)Math.cos(angle) * speed;
            p.speed_y = (float)Math.sin(angle) * speed;
            p.accel_x = getRandomVar(e.var_accel_x) + e.accel_x;
            p.accel_y = getRandomVar(e.var_accel_y) + e.accel_y;

            p.startColor[0] = getRandomVar(e.var_startColor[0]) + e.startColor[0];
            p.startColor[1] = getRandomVar(e.var_startColor[1]) + e.startColor[1];
            p.startColor[2] = getRandomVar(e.var_startColor[2]) + e.startColor[2];
            p.startColor[3] = getRandomVar(e.var_startColor[3]) + e.startColor[3];

            p.rangeColor[0] = getRandomVar(e.var_endColor[0]) + e.endColor[0] - p.startColor[0];
            p.rangeColor[1] = getRandomVar(e.var_endColor[1]) + e.endColor[1] - p.startColor[1];
            p.rangeColor[2] = getRandomVar(e.var_endColor[2]) + e.endColor[2] - p.startColor[2];
            p.rangeColor[3] = getRandomVar(e.var_endColor[3]) + e.endColor[3] - p.startColor[3];

            p.life = 0;
            p.size = e.size + getRandomVar(e.var_size);


            // Fill the point sprites data
            int o = i * PointSprites.ELEMENTS_PER_POINT;

            e.pointSpritesData[o] = p.x;
            e.pointSpritesData[o+1] = p.y;

            float f = p.life/e.life;
            e.pointSpritesData[o+2] = p.startColor[0] + f * p.rangeColor[0];
            e.pointSpritesData[o+3] = p.startColor[1] + f * p.rangeColor[1];
            e.pointSpritesData[o+4] = p.startColor[2] + f * p.rangeColor[2];
            e.pointSpritesData[o+5] = p.startColor[3] + f * p.rangeColor[3];

            e.pointSpritesData[o+6] = p.size;
        }
        e.numParticles += num;
    }

    public void kill(Emitter e, int p) {
        if (p == e.numParticles - 1)
            e.particles[p] = null;
        else
            e.particles[p] = e.particles[e.numParticles - 1];
        e.numParticles --;
    }

    @Override
    public void update(float dt) {

        for (Entity entity : mEntities) {
            Emitter e = entity.get(Emitter.class);

            if (e.emitNext) {
                e.emissionTime += dt;
                float et = 1 / e.emissionRate;
                if (e.emissionTime >= et) {
                    e.emissionTime -= et;
                    emit(e, 1);
                    if (e.emitOnlyOnce)
                        e.emitNext = false;
                }
            }

            for (int i = 0; i < e.numParticles; ++i) {
                Emitter.Particle p = e.particles[i];

                if (p==null)
                    continue;

                if (p.life >= e.life) {
                    kill(e, i);
                    continue;
                }
                p.life += dt;

                p.speed_x += p.accel_x * dt;
                p.speed_y += p.accel_y * dt;

                p.x = p.x + p.speed_x * dt;
                p.y = p.y + p.speed_y * dt;


                // Fill the point sprites data
                int o = i * PointSprites.ELEMENTS_PER_POINT;

                e.pointSpritesData[o] = p.x;
                e.pointSpritesData[o + 1] = p.y;

                float f = p.life / e.life;
                e.pointSpritesData[o + 2] = p.startColor[0] + f * p.rangeColor[0];
                e.pointSpritesData[o + 3] = p.startColor[1] + f * p.rangeColor[1];
                e.pointSpritesData[o + 4] = p.startColor[2] + f * p.rangeColor[2];
                e.pointSpritesData[o + 5] = p.startColor[3] + f * p.rangeColor[3];

                e.pointSpritesData[o + 6] = p.size;
            }
        }
    }

    private void draw(Entity entity) {
        Emitter e = entity.get(Emitter.class);
        Transformation t = entity.get(Transformation.class);
        if (e.additiveBlend) {
            e.pointSprites.getRenderer().setAdditiveBlending();
        }
        e.pointSprites.getRenderer().disableDepth();

        e.pointSprites.draw(e.pointSpritesData, t.x, t.y, t.angle+e.offsetAngle, e.numParticles);

        if (e.additiveBlend) {
            e.pointSprites.getRenderer().setAlphaBlending();
        }
        e.pointSprites.getRenderer().enableDepth();
    }


    private List<Entity> mPostDrawing = new ArrayList<>();

    @Override
    public void draw() {
        mPostDrawing.clear();
        for (Entity entity : mEntities) {
            Emitter e = entity.get(Emitter.class);
            if (e.postDrawn)
                mPostDrawing.add(entity);
            else
                draw(entity);
        }
    }

    @Override
    public void postDraw() {
        for (Entity e: mPostDrawing)
            draw(e);
    }
}
