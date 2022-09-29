package com.mygdx.game.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.engines.MyECSEngine;
import lombok.Getter;
import lombok.Setter;

import static com.mygdx.game.Constants.PPM;

@Getter
@Setter
public class RenderSystem extends EntitySystem {
    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;
    private MyECSEngine engine;
    private SpriteBatch batch;

    public RenderSystem(MyECSEngine engine) {
        this.engine = engine;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1000, 800);
        camera.update();
        renderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();
    }

    public void cameraUpdate() {
        camera.position.set(0, 0, 0);
        camera.update();
    }

    @Override
    public void update(float deltaTime) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cameraUpdate();
        batch.setProjectionMatrix(camera.combined);
        Vector2 position = new Vector2();
        Array<Body> bodies = new Array<>();
        engine.getWorld().getBodies(bodies);
        bodies.forEach(body -> {
            if (body.getUserData().equals("Player")) position.set(body.getPosition());
        });
        batch.begin();
        batch.draw(new Texture("badlogic.jpg"), position.x*PPM-128, position.y*PPM-128);
        batch.end();

        renderer.render(engine.getWorld(), camera.combined.scl(PPM));
    }
}
