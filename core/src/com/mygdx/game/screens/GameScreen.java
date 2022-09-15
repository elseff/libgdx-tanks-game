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
import com.mygdx.game.Box;
import com.mygdx.game.GameObjectType;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Panzer;

import static com.mygdx.game.Constants.PPM;

public class GameScreen implements Screen {
    private final MyGdxGame game;
    public Array<Box> boxes = new Array<>();
    public Texture groundTexture;
    public Panzer me;
    public Array<Panzer> enemies;
    public World world;
    public Box2DDebugRenderer debugRenderer;
    public Timer timer;
    public GameScreen(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        groundTexture = new Texture("ground.png");
        world = new World(new Vector2(0, 0), true);
        game.font = game.createFont(18);
        debugRenderer = new Box2DDebugRenderer();

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
        game.createBox(groundTexture.getWidth() / 2f, 0, groundTexture.getWidth() / 2f, 1, true, world);
        game.createBox(groundTexture.getWidth() / 2f, groundTexture.getHeight(), groundTexture.getWidth() / 2f, 1, true, world);
        game.createBox(0, groundTexture.getHeight() / 2f, 1, groundTexture.getHeight() / 2f, true, world);
        game.createBox(groundTexture.getWidth(), groundTexture.getHeight() / 2f, 1, groundTexture.getHeight() / 2f, true, world);
        timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                enemies.forEach(enemy -> enemy.setHealthPoints(enemy.getHealthPoints() - 3f));
                me.setHealthPoints(me.getHealthPoints() - 5f);
            }
        }, 1, 1);
    }

    public void update(float delta) {
        scaleUpdate(delta);
        inputUpdate();
        cameraUpdate();
        game.inputProcessor.updateMousePos();

        Vector2 mouseDirection = new Vector2();
        mouseDirection.x = game.camera.unproject(new Vector3(game.inputProcessor.getMousePos(), 0)).sub(me.getPosition().x * PPM).x;
        mouseDirection.y = game.camera.unproject(new Vector3(game.inputProcessor.getMousePos(), 0)).sub(me.getPosition().y * PPM).y;
        me.rotateTo(mouseDirection.angleDeg());
        me.move();

        for (int i = 0; i < enemies.size; i++) {
            Panzer currentEnemy = enemies.get(i);
            currentEnemy.move();
            Vector2 playerPosition = new Vector2();
            playerPosition.set(me.getPosition().x*PPM,me.getPosition().y*PPM);
            Vector2 currentEnemyPosition = new Vector2();
            currentEnemyPosition.set(currentEnemy.getPosition().x*PPM, currentEnemy.getPosition().y*PPM);
            float angle = playerPosition.sub(currentEnemyPosition).angleDeg();
            currentEnemy.rotateTo(angle);
        }

        game.batch.setProjectionMatrix(game.camera.combined);
        game.shapeRenderer.setProjectionMatrix(game.camera.combined);

        world.step(1 / 60f, 6, 2);
    }

    public void inputUpdate() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    public void cameraUpdate() {
        game.camera.position.x = me.getPosition().x * PPM;
        game.camera.position.y = me.getPosition().y * PPM;
        game.camera.update();
    }

    private void scaleUpdate(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_SUBTRACT) && MyGdxGame.SCALE >= 0.7) {
            MyGdxGame.SCALE -= delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_ADD) && MyGdxGame.SCALE <= 1.3) {
            MyGdxGame.SCALE += delta;
        }
        game.camera.setToOrtho(false, MyGdxGame.SCREEN_WIDTH / MyGdxGame.SCALE, MyGdxGame.SCREEN_HEIGHT / MyGdxGame.SCALE);
        game.camera.update();
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(groundTexture, 0, 0);
        for (int i = 0; i < boxes.size; i++) {
            Box currentBox = boxes.get(i);
            currentBox.render(game.batch);
        }
        for (int i = 0; i < enemies.size; i++) {
            Panzer currentEnemy = enemies.get(i);
            currentEnemy.render(game.batch, game.font, game.shapeRenderer);
        }
        me.render(game.batch, game.font, game.shapeRenderer);
        game.batch.end();

        //VERY IMPORTANT!!!!!!
        Vector2 mousePosition = new Vector2();
        mousePosition.set(game.viewport.getCamera().unproject(new Vector3(game.inputProcessor.getMousePos(), 0)).x, game.viewport.getCamera().unproject(new Vector3(game.inputProcessor.getMousePos(), 0)).y);
        //VERY IMPORTANT!!!!!!

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.circle(mousePosition.x, mousePosition.y, 10);
        game.shapeRenderer.end();
        if (me.getHealthPoints() <= 0) {
            game.setScreen(new GameOverScreen(game));
        }
        debugRenderer.render(world, game.camera.combined.scl(PPM));
    }

    @Override
    public void resize(int width, int height) {
        game.camera.setToOrtho(false, MyGdxGame.SCREEN_WIDTH / MyGdxGame.SCALE, MyGdxGame.SCREEN_HEIGHT / MyGdxGame.SCALE);
        game.camera.update();
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
        boxes.removeAll(boxes,true);
        groundTexture.dispose();
        me.dispose();
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).dispose();
        }
        enemies.removeAll(enemies,true);
        world.dispose();
        debugRenderer.dispose();
        timer.stop();
    }
}
