package com.mygdx.game.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.engines.MyECSEngine;

public class MovementSystem extends EntitySystem {
    private final MyECSEngine engine;
    private final Vector2 direction = new Vector2();
    //speed of rotating a tower
    private final float rotationalVelocity = 5f;
    //speed of moving
    private final float speed = 5f;

    public MovementSystem(MyECSEngine engine) {
        this.engine = engine;
    }

    @Override
    public void update(float deltaTime) {
        Array<Body> bodies = new Array<>();
        engine.getWorld().getBodies(bodies);
        Body body = null;
        for (Body body1 : bodies) {
            if (body1.getUserData().equals("Player"))
                body = body1;
        }
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
}
