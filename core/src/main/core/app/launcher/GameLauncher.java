package core.app.launcher;

import com.badlogic.gdx.Gdx;
import core.app.GameContext;
import core.app.GameLoop;
import core.app.GameStart;
import core.app.Vars;
import core.app.cores.AppListener;
import core.app.cores.GameCore;

import static core.app.Vars.*;

public class GameLauncher extends GameCore {
    private GameLoop gameLoop;

    private boolean loaded = false;

    @Override
    public void init() {
        Vars.init();
        GameStart.createLoadingContext();
    }

    @Override
    public void render() {
        if (!loaded) {
            if (assets.updateLoading()) {
                GameStart.finishGameStart();
                gameLoop = new GameLoop();
                gameLoop.resize(
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight()
                );

                loaded = true;
            }
            return;
        }

        gameLoop.update();
        gameLoop.render();
    }

    @Override
    public void resize(int width, int height) {
        if (gameLoop != null) {
            gameLoop.resize(
                width, height
            );
        }
    }
    @Override
    public void pause () {
        if (gameLoop != null) gameLoop.pause();
    }
    @Override
    public void resume () {
        if (gameLoop != null) gameLoop.resume();
    }

    @Override
    public void dispose() {
        if (gameLoop != null) {
            gameLoop.dispose();
            return;
        }

        if (context != null && context.assets != null) context.assets.dispose();
    }

    public void add(AppListener child) {
        super.add(child);
    }

    public GameContext getContext() {
        return context;
    }
    public GameLoop getGameLoop() {
        return gameLoop;
    }

}
