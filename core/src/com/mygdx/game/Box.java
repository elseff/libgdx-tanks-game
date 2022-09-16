package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.mygdx.game.Constants.PPM;

/**
 * Box class
 */
public class Box {
    private Vector2 position;
    private final TextureRegion texture;
    private final Body body;

    public Box(int x, int y, World world) {
        position = new Vector2();
        this.position.set(x, y);
        this.texture = new TextureRegion(new Texture("box.png"));

        BodyDef bodyDef = new BodyDef();

        bodyDef.fixedRotation = false;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(15 / PPM, 15 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 100.0f;
        fixtureDef.restitution = .01f;
        fixtureDef.shape = shape;
        fixtureDef.friction = 1f;

        body.createFixture(fixtureDef);
        body.setLinearDamping(5.0f);
        body.setAngularDamping(5.0f);
        shape.dispose();
    }

    public void update() {
        position = body.getPosition();
    }

    public void render(SpriteBatch batch) {
        update();
        batch.draw(texture,
                body.getPosition().x * PPM - texture.getTexture().getWidth() / 2f,
                body.getPosition().y * PPM - texture.getTexture().getHeight() / 2f,
                texture.getRegionWidth() / 2f,
                texture.getRegionHeight() / 2f,
                texture.getRegionWidth(),
                texture.getRegionHeight(),
                1.0f,
                1.0f,
                body.getTransform().getOrientation().angleDeg());
    }
}
