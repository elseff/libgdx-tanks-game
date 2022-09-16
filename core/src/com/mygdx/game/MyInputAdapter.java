package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

/**
 * Mouse move listener
 */
@Getter
public class MyInputAdapter extends InputAdapter {
    private final Vector2 mousePos = new Vector2();

    public void updateMousePos() {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        mousePos.set(x, y);
    }
}
