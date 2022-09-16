package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.MyGdxGame;

/**
 * Screen of Game Over
 */
public class GameOverScreen implements Screen {
    private final MyGdxGame game;
    private Timer timer;

    public GameOverScreen(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.setFont(game.createFont(50));
        timer = new Timer();
        game.getCamera().position.set(0, 0, 0);
        game.getCamera().update();
        game.getBatch().setProjectionMatrix(game.getCamera().combined);
        //after 5 seconds repeat a game and set GameScreen
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new GameScreen(game));
            }
        }, 5);
    }

    public void update(float delta) {
        scaleUpdate(delta);
        inputUpdate();
        cameraUpdate();
        game.getInputProcessor().updateMousePos();
        game.getBatch().setProjectionMatrix(game.getCamera().combined);
        game.getShapeRenderer().setProjectionMatrix(game.getCamera().combined);
    }

    public void inputUpdate() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    public void cameraUpdate() {
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
        Gdx.gl.glClearColor(.21115f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getBatch().begin();
        game.getFont().draw(game.getBatch(), "GAME OVER", MyGdxGame.SCREEN_WIDTH / 2f - 100, MyGdxGame.SCREEN_HEIGHT / 1.9f);
        game.setFont(game.createFont(25));
        game.getFont().draw(game.getBatch(), "restart in 5 seconds", MyGdxGame.SCREEN_WIDTH / 2f - 77, MyGdxGame.SCREEN_HEIGHT / 2.2f);
        game.setFont(game.createFont(50));
        game.getBatch().end();
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
        timer.stop();
    }
}
