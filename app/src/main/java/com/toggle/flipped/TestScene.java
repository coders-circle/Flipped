package com.toggle.flipped;


import com.toggle.katana2d.*;

public class TestScene extends Scene {

    private Entity testEntity1, testEntity2;

    @Override
    public void onInit() {
        // Add the systems
        mSystems.add(new RenderSystem());

        // Add resources to the manager
        Manager manager = Manager.getManager();

        // Yellow colored sprite
        int spr0 = manager.addSprite(
                new GLSprite(mGame.getRenderer(), null, new float[]{1, 1, 0, 1}, 64, 128)
        );
        // Test sprite from image
        int spr1 = manager.addSprite(
                new GLSprite(mGame.getRenderer(), mGame.getRenderer().loadTexture(R.drawable.test_spr), 124 - 40, 106)
        );

        // Create first test entity with an yellow sprite and rotated initial transformation
        Entity testEntity1 = new Entity();
        testEntity1.add(new Sprite(manager.getSprite(spr0)));
        testEntity1.add(new Transformation(50, 50, 20));
        addEntity(testEntity1);

        // Create second test entity with animated sprite-sheet
        // The sprite-sheet data has been obtained with the help of an image editing program
        Sprite.SpriteSheetData ssd = new Sprite.SpriteSheetData();
        ssd.offsetX = 40;
        ssd.hSpacing = 150-124;
        ssd.numCols =  5;
        ssd.imgWidth = 124-40;
        ssd.imgHeight = 106;
        ssd.index = 1;
        ssd.animationSpeed = 6;
        testEntity2 = new Entity();
        testEntity2.add(new Sprite(manager.getSprite(spr1), ssd));
        testEntity2.add(new Transformation(150, 150, 0));
        addEntity(testEntity2);
    }

    @Override
    public void onUpdate(double deltaTime) {
        mGame.getRenderer().getCamera().angle += deltaTime*10;
        mGame.getRenderer().getCamera().x += deltaTime*50;
        testEntity2.get(Transformation.class).x += deltaTime*50;
    }
}
