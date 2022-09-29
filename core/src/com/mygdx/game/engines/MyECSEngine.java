package com.mygdx.game.engines;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.components.BodyComponent;
import lombok.Getter;

import static com.mygdx.game.Constants.PPM;

@Getter
public class MyECSEngine extends Engine {
    private final World world;

    public MyECSEngine() {
        world = new World(new Vector2(0, 0), false);
    }

    public Entity createEntity(float x, float y, float width, float height, boolean isStatic, boolean isPLayer) {
        Entity entity = this.createEntity();

        BodyComponent bodyComponent = this.createComponent(BodyComponent.class);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = !isStatic ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.fixedRotation = false;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / PPM / 2, height / PPM / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;

        Body body = world.createBody(bodyDef);
        body.setLinearDamping(2.0f);
        body.setAngularDamping(2.0f);
        body.createFixture(fixtureDef);
        if (isPLayer)
            body.setUserData("Player");
        else
            body.setUserData("Entity");
        bodyComponent.setBody(body);

        entity.add(bodyComponent);

        this.addEntity(entity);

        return entity;
    }

    @Override
    public void update(float deltaTime) {
        world.step(1 / 60f, 8, 2);
        super.update(deltaTime);
    }
}
