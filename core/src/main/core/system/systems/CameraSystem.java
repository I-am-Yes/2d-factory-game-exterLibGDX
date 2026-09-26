package core.system.systems;

import core.app.cores.AppListener;
import core.config.ScriptConfigLoader;
import core.config.api.CameraScriptApi;
import core.config.camera.CameraSettings;
import core.system.context.CameraContext;
import core.app.GameContext;
import core.system.ContextProvider;
import core.controller.camera.CameraController;
import core.system.context.PlayerContext;
import core.controller.camera.CursorController;
import core.app.cores.GameSysCycle;

import java.util.Map;

import static core.app.Vars.*;

public class CameraSystem implements AppListener, GameSysCycle {

    private CameraSettings cameraSettings;

    private CursorController cursor;

    public CameraSystem() {


        this.cursor = new CursorController();
        cursor.loadCursor();

        this.cameraSettings = new CameraSettings();

        ScriptConfigLoader.loadAll(
            "scripts",
            Map.of("camera", new CameraScriptApi(cameraSettings))
        );

        cameraController = new CameraController(cameraSettings);

    }

    @Override
    public void update() {

        cameraController.update();
    }

    @Override
    public void render() {
        AppListener.super.render();
    }

    @Override
    public void resize(int width, int height) {
        AppListener.super.resize(width, height);
    }

    @Override
    public void pause() {
        AppListener.super.pause();
    }

    @Override
    public void resume() {
        AppListener.super.resume();
    }

    @Override
    public void dispose() {
        AppListener.super.dispose();
    }
}
