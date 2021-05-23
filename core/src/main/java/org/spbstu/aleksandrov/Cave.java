package org.spbstu.aleksandrov;


import com.badlogic.gdx.Game;
import org.spbstu.aleksandrov.view.screens.GameScreen;

public class Cave extends Game {

    private final Game game;

    public Cave() {
        game = this;
    }

    @Override
    public void create() {
        setScreen(new GameScreen(game));
    }

    public void render() {
        super.render();
    }
}
