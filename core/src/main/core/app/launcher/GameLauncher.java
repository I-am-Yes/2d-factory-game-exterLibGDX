package core.app.launcher;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import core.app.GameStart;
import core.app.cores.AppListener;
import core.app.extras.LoadingScreen;
import core.app.vars.Cores;
import core.app.cores.GameCore;
import core.controller.camera.ScreenshotCapture;
import core.event.events.AppEvent;

import static core.app.vars.Vars.*;
import static core.app.vars.Cores.*;

public class GameLauncher extends GameCore {

    private boolean loaded = false;

    @Override
    public void init() {
        loadingScreen = LoadingScreen.init();
        start = new GameStart();
        start.startGame();
        add(start);
    }

    @Override
    public void update() {
        if (!loaded) {
            if (assets.updateLoading()) {
                start.finishGameStart();

                AppEvent.GameLoaded.fire();
                loaded = true;
            }
            return;
        }

        super.update();

        world.update();

        drawScreen();

        Cores.input.endFrame();
    }

    @Override
    public void dispose() {
        if (assets != null) assets.dispose();
        super.dispose();
    }

    private void drawScreen() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();

        world.drawCached(cameraController);

        //BEGIN batch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        //TODO: this

        batch.end();
        //END batch


        //BEGIN shapeRender
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        //Note: add another ShapeType if needed.

        //TODO: this

        shape.end();
        //END shapeRender

        ScreenshotCapture.captureIfRequested();

    }

    @Override
    public void add(AppListener child) {
        super.add(child);
    }
}
