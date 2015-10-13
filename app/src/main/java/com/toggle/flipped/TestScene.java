package com.toggle.flipped;


import com.toggle.katana2d.*;

public class TestScene extends Scene {

    public TestScene(Game game) {
        super(game);
    }

    private Entity testEntity1 = new Entity(), testEntity2 = new Entity();

    @Override
    public void onInit() {
        mSystems.add(new RenderSystem());

        // Create first test entity with an yellow sprite and rotated initial transformation
        testEntity1.addComponent(
                new Sprite(new GLSprite(mGame.getRenderer(), null, new float[]{1, 1, 0, 1}, 64, 128))
        );
        testEntity1.addComponent(
                new Transformation(50, 50, 20)
        );
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
        testEntity2.addComponent(
                new Sprite(
                        new GLSprite(mGame.getRenderer(), mGame.getRenderer().loadTexture(com.toggle.katana2d.R.drawable.test_spr), 124-40, 106), ssd
                )
        );
        testEntity2.addComponent(
                new Transformation(150, 150, 0)
        );
        addEntity(testEntity2);
    }

    @Override
    public void onUpdate(double deltaTime) {
        mGame.getRenderer().getCamera().angle = 180;
        mGame.getRenderer().getCamera().x += deltaTime*50;
        testEntity2.getComponent(Transformation.class).x += deltaTime*50;
    }
}
