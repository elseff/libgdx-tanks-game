package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.screens.GameOverScreen;
import com.mygdx.game.screens.GameScreen;
import lombok.Getter;
import lombok.Setter;

import static com.mygdx.game.Constants.PPM;

/**
 * Main Game class
 */
@Getter
public class MyGdxGame extends Game {
    public static final int SCREEN_WIDTH = 1000;
    public static final int SCREEN_HEIGHT = 800;
    public static float SCALE = 1f;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private MyInputAdapter inputProcessor;

    private ShapeRenderer shapeRenderer;

    @Setter
    private BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        shapeRenderer = new ShapeRenderer();

        inputProcessor = new MyInputAdapter();
        Gdx.input.setInputProcessor(inputProcessor);

        font = createFont(18);

        setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    //creating a box in the world with parameters
    public void createBox(float x, float y, float width, float height, boolean isStatic, World world) {
        Body body;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.fixedRotation = false;
        bodyDef.gravityScale = 10f;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / PPM, height / PPM);

        body.createFixture(shape, 10.0f);
        shape.dispose();

    }

    //generation of BitMapFont with white color and size parameter
    public BitmapFont createFont(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + "Roboto-Medium.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = Color.WHITE;

        return generator.generateFont(parameter);
    }

}
