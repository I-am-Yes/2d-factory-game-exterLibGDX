package core.app.launcher;

import com.badlogic.gdx.Gdx;
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
        start.createLoadingContext();
    }

    @Override
    public void update() {
        if (!loaded) {
            if (assets.updateLoading()) {
                start.finishGameStart();

                loaded = true;
            }
            return;
        }

        super.update();
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
        super.pause();
    }
    @Override
    public void resume () {
        super.resume();
    }

    @Override
    public void dispose() {
        if (gameLoop != null) {
            gameLoop.dispose();
            return;
        }

        if (assets != null) assets.dispose();

        super.dispose();
    }

    public void add(AppListener child) {
        super.add(child);
    }

}
