package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.*;

import static com.mygdx.game.Constants.PPM;

/**
 * Main Game Screen
 */
public class GameScreen implements Screen {
    private final MyGdxGame game;
    private Array<Box> boxes = new Array<>();
    //background texture
    private Texture groundTexture;
    private Panzer me;
    private Array<Panzer> enemies;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Timer timer;
    private B2dContactListener contactListener;

    public GameScreen(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        groundTexture = new Texture("ground.png");
        world = new World(new Vector2(0, 0), true);
        game.setFont(game.createFont(18));
        debugRenderer = new Box2DDebugRenderer();
        contactListener = new B2dContactListener();
        world.setContactListener(contactListener);
        enemies = new Array<>();

        for (int i = 2; i < 7; i++) {
            enemies.add(new Panzer(world, i * 250, 550, GameObjectType.ENEMY));
        }
        me = new Panzer(world, 400, 400);
        for (int i = 1; i < 7; i++) {
            for (int j = 1; j < 7; j++) {
                boxes.add(new Box(i * 90 + 1100, j * 90, world));
            }
        }

        //creating a world box
        game.createBox(groundTexture.getWidth() / 2f, 0, groundTexture.getWidth() / 2f, 1, true, world);
        game.createBox(groundTexture.getWidth() / 2f, groundTexture.getHeight(), groundTexture.getWidth() / 2f, 1, true, world);
        game.createBox(0, groundTexture.getHeight() / 2f, 1, groundTexture.getHeight() / 2f, true, world);
        game.createBox(groundTexture.getWidth(), groundTexture.getHeight() / 2f, 1, groundTexture.getHeight() / 2f, true, world);

        timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                enemies.forEach(enemy -> enemy.setHealthPoints(enemy.getHealthPoints() - 3f));
                me.setHealthPoints(me.getHealthPoints() - .5f);
            }
        }, 1, 1);
    }

    public void update(float delta) {
        scaleUpdate(delta);
        inputUpdate();
        cameraUpdate();
        game.getInputProcessor().updateMousePos();
        //mouse position in physical box2d world
        Vector2 mouseDirection = new Vector2();
        mouseDirection.x = game.getCamera().unproject(new Vector3(game.getInputProcessor().getMousePos(), 0)).sub(me.getPosition().x * PPM).x;
        mouseDirection.y = game.getCamera().unproject(new Vector3(game.getInputProcessor().getMousePos(), 0)).sub(me.getPosition().y * PPM).y;
        me.rotateTo(mouseDirection.angleDeg());

        me.move();

        for (int i = 0; i < enemies.size; i++) {
            Panzer currentEnemy = enemies.get(i);
            currentEnemy.move();
            //rotating a enemy tanks on player
            Vector2 playerPosition = new Vector2();
            playerPosition.set(me.getPosition().x * PPM, me.getPosition().y * PPM);
            Vector2 currentEnemyPosition = new Vector2();
            currentEnemyPosition.set(currentEnemy.getPosition().x * PPM, currentEnemy.getPosition().y * PPM);
            float angle = playerPosition.sub(currentEnemyPosition).angleDeg();
            currentEnemy.rotateTo(angle);
        }

        game.getBatch().setProjectionMatrix(game.getCamera().combined);
        game.getShapeRenderer().setProjectionMatrix(game.getCamera().combined);

        world.step(1 / 60f, 6, 2);
    }

    public void inputUpdate() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    public void cameraUpdate() {
        //set camera position to player position
        game.getCamera().position.x = me.getPosition().x * PPM;
        game.getCamera().position.y = me.getPosition().y * PPM;
        game.getCamera().update();
    }

    private void scaleUpdate(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_SUBTRACT) && MyGdxGame.SCALE >= 0.7) {
            MyGdxGame.SCALE -= delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_ADD) && MyGdxGame.SCALE <= 1.3) {
            MyGdxGame.SCALE += delta;
        }
        game.getCamera().setToOrtho(false, MyGdxGame.SCREEN_WIDTH / MyGdxGame.SCALE, MyGdxGame.SCREEN_HEIGHT / MyGdxGame.SCALE);
        game.getCamera().update();
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        game.getBatch().draw(groundTexture, 0, 0);
        for (int i = 0; i < boxes.size; i++) {
            Box currentBox = boxes.get(i);
            currentBox.render(game.getBatch());
        }
        for (int i = 0; i < enemies.size; i++) {
            Panzer currentEnemy = enemies.get(i);
            currentEnemy.render(game.getBatch(), game.getFont(), game.getShapeRenderer());
        }
        me.render(game.getBatch(), game.getFont(), game.getShapeRenderer());
        game.getBatch().end();

        //mouse position in physical box2d world
        //VERY IMPORTANT!!!!!!
        Vector2 mousePosition = new Vector2();
        mousePosition.set(game.getViewport().getCamera().unproject(new Vector3(game.getInputProcessor().getMousePos(), 0)).x,
                game.getViewport().getCamera().unproject(new Vector3(game.getInputProcessor().getMousePos(), 0)).y);
        //VERY IMPORTANT!!!!!!

        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        game.getShapeRenderer().circle(mousePosition.x, mousePosition.y, 10);
        game.getShapeRenderer().end();
        if (me.getHealthPoints() <= 0) {
            game.setScreen(new GameOverScreen(game));
        }
        debugRenderer.render(world, game.getCamera().combined.scl(PPM));
    }

    @Override
    public void resize(int width, int height) {
        game.getCamera().setToOrtho(false, MyGdxGame.SCREEN_WIDTH / MyGdxGame.SCALE, MyGdxGame.SCREEN_HEIGHT / MyGdxGame.SCALE);
        game.getCamera().update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        boxes.removeAll(boxes, true);
        groundTexture.dispose();
        me.dispose();
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).dispose();
        }
        enemies.removeAll(enemies, true);
        world.dispose();
        debugRenderer.dispose();
        timer.stop();
    }
}
