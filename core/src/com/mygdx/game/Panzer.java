package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.mygdx.game.Constants.PPM;

/**
 * Tank class
 */
public class Panzer {
    private Vector2 position = new Vector2();
    private TextureRegion textureTower;
    private TextureRegion textureBody;
    private float angleTower = 0;
    private final Vector2 direction = new Vector2();
    private final GameObjectType type;
    private final Body body;
    //speed of rotating a tower
    private final float rotationalVelocity = 5f;
    //speed of moving
    private final float speed = 30f;
    private float healthPoints;

    public Panzer(World world, float x, float y) {
        this(world, x, y, GameObjectType.PLAYER);
    }

    public Panzer(World world, float x, float y, GameObjectType type) {
        position.set(x, y);
        healthPoints = 100f;
        this.type = type;

        if (type.equals(GameObjectType.PLAYER)) {
            textureTower = new TextureRegion(new Texture("panzer_player_tower.png"));
            textureBody = new TextureRegion(new Texture("panzer_player_body.png"));
        } else if (type.equals(GameObjectType.ENEMY)) {
            textureTower = new TextureRegion(new Texture("panzer_enemy_tower.png"));
            textureBody = new TextureRegion(new Texture("panzer_enemy_body.png"));
        }

        BodyDef bodyDef = new BodyDef();

        bodyDef.fixedRotation = false;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);
        if (!type.equals(GameObjectType.PLAYER)) {
            bodyDef.angle = new Vector2(0, -5).angleRad();
        }
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(textureBody.getRegionWidth() / 2f / PPM, textureBody.getRegionHeight() / 2f / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 150.0f;
        fixtureDef.shape = shape;
        fixtureDef.friction = 1000f;
        fixtureDef.restitution = .1f;

        body.createFixture(fixtureDef);
        body.setLinearDamping(70.0f);
        body.setAngularDamping(70.0f);

        shape.dispose();
    }


    public void render(Batch batch, BitmapFont font, ShapeRenderer shapeRenderer) {
        batch.draw(textureBody,
                body.getPosition().x * PPM - textureBody.getRegionWidth() / 2f,
                body.getPosition().y * PPM - textureBody.getRegionHeight() / 2f,
                textureBody.getRegionWidth() / 2f,
                textureBody.getRegionHeight() / 2f,
                textureBody.getRegionWidth(),
                textureBody.getRegionHeight(),
                1.0f,
                1.0f,
                body.getTransform().getOrientation().angleDeg());
        batch.draw(textureTower,
                body.getPosition().x * PPM - textureTower.getTexture().getWidth() / 4f,
                body.getPosition().y * PPM - textureTower.getTexture().getHeight() / 2f,
                getTextureTower().getRegionWidth() / 4f,
                getTextureTower().getRegionHeight() / 2f,
                getTextureTower().getRegionWidth(),
                getTextureTower().getRegionHeight(),
                1,
                1,
                angleTower);
        font.setColor(Color.WHITE);
        font.draw(batch,
                type.equals(GameObjectType.PLAYER) ? ("Player: " + new Vector2(MathUtils.ceil(body.getPosition().x * PPM), MathUtils.ceil(body.getPosition().y * PPM))) :
                        ("Enemy: " + new Vector2(MathUtils.ceil(body.getPosition().x * PPM), MathUtils.ceil(body.getPosition().y * PPM))),
                body.getPosition().x * PPM - 50,
                body.getPosition().y * PPM + getTextureTower().getRegionHeight() + 50);
        Color healthPointBarColor = new Color();
        if (healthPoints > 70) {
            font.setColor(Color.GREEN);
            healthPointBarColor.set(Color.GREEN);
        }
        if (healthPoints >= 30 && healthPoints <= 70) {
            font.setColor(Color.YELLOW);
            healthPointBarColor.set(Color.YELLOW);
        }
        if (healthPoints < 30) {
            font.setColor(Color.RED);
            healthPointBarColor.set(Color.RED);
        }
        font.draw(batch, "HP: " + ((int) healthPoints),
                body.getPosition().x * PPM - 50,
                body.getPosition().y * PPM + getTextureTower().getRegionHeight() + 35);
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(body.getPosition().x * PPM - 50,
                body.getPosition().y * PPM - getTextureTower().getRegionHeight() - 20,
                healthPoints, 10, healthPointBarColor, healthPointBarColor, healthPointBarColor, healthPointBarColor);
        shapeRenderer.end();
        batch.begin();
    }

    //moving tank if he is a player
    public void move() {
        if (type == GameObjectType.PLAYER) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                    direction.rotateDeg(-rotationalVelocity);
                } else {
                    direction.rotateDeg(rotationalVelocity);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                if (Gdx.input.isKeyPressed(Input.Keys.W))
                    direction.rotateDeg(-rotationalVelocity);
                else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                    direction.rotateDeg(rotationalVelocity);
                } else {
                    direction.rotateDeg(-rotationalVelocity);
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                if (direction.isZero()) direction.x += speed;
                body.setLinearVelocity(direction.x, direction.y);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if (direction.isZero()) direction.x += speed;
                body.setLinearVelocity(-direction.x / 1.7f, -direction.y / 1.7f);
            }
            //apply changes of position and rotate depends of direction
            body.setTransform(body.getPosition().x, body.getPosition().y, direction.angleRad());
        }
        position = body.getPosition();
    }

    //rotate a tower by angle in degrees
    public void rotateTo(float angle) {
        this.angleTower = angle;
    }

    public Vector2 getPosition() {
        return position;
    }

    public TextureRegion getTextureTower() {
        return textureTower;
    }

    public float getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(float healthPoints) {
        this.healthPoints = healthPoints;
    }

    public void dispose() {
        textureTower.getTexture().dispose();
        textureBody.getTexture().dispose();
    }
}
