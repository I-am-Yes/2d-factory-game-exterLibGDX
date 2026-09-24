package core.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private LoadingScreen loadingScreen;
    private GameContext context;
    private GameLoop gameLoop;

    private boolean loaded = false;

    @Override
    public void create() {
        loadingScreen = new LoadingScreen();
        context = GameStart.createLoadingContext();
    }

    @Override
    public void render() {
        if (!loaded) {
            float progress = context.assets.getProgress();
            loadingScreen.render(progress);

            if (context.assets.updateLoading()) {
                GameStart.finishGameStart(context);
                gameLoop = new GameLoop(context);
                gameLoop.resize(
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight()
                );

                loadingScreen.dispose();
                loadingScreen = null;
                loaded = true;
            }
            return;
        }

        gameLoop.update();
        gameLoop.render();
    }

    @Override
    public void resize(int width, int height) {
        if (loadingScreen != null) {
            loadingScreen.resize(width, height);
        }

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

        if (loadingScreen != null) loadingScreen.dispose();

        if (context != null && context.assets != null) context.assets.dispose();
    }


    public GameContext getContext() {
        return context;
    }
    public GameLoop getGameLoop() {
        return gameLoop;
    }

}
