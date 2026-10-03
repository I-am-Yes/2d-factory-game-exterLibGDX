package core.app.launcher;

import com.badlogic.gdx.Gdx;

import arcane.*;
import arcane.graphics.Artist;
import core.app.GameStart;
import core.app.extras.LoadingScreen;
import core.event.AppEvent;

//TODO: make this ss an event caller
import core.controller.camera.ScreenshotCapture;

import static arcane.Cores.*;
import static core.app.vars.Apps.*;
import static core.app.vars.Vars.*;

public class GameLauncher extends GameCore {

    private boolean loaded = false;

    @Override
    public void init() {
        loadingScreen = LoadingScreen.init();
        start = new GameStart();
        start.startGame();
        add(start);

        super.init();
    }

    @Override
    public void update() {
        if (!loaded) {
            if (assets.updateLoading()) {
                start.finishGameStart();
                viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
                AppEvent.GameLoaded.fire();
                loaded = true;
            }
            return;
        }

        Artist.screenClear(viewport);

        batch.setProjectionMatrix(camera.combined);
        shape.setProjectionMatrix(camera.combined);

        super.update();

        input.endFrame();

        ScreenshotCapture.captureIfRequested();
    }

    @Override
    public void pause() {
        Events.on(AppEvent.GamePaused.class, event -> {
            Time.pause();
            super.pause();
        });
    }

    @Override
    public void resume() {
        Events.on(AppEvent.ResumeGame.class, event -> {
            Time.resume();
            super.resume();
        });
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null)
            viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        super.resize(width, height);
    }

    @Override
    public void dispose() {
        if (assets != null) assets.dispose();
        super.dispose();
    }

    @Override
    public void add(ApplicationListener child) {
        super.add(child);
    }
}
