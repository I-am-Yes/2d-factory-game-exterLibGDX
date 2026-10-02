package core.system.systems;

import arcane.AppListener;
import core.config.ScriptConfigLoader;
import core.config.api.CameraScriptApi;
import core.config.camera.CameraSettings;
import core.controller.camera.CameraController;
import core.controller.camera.CursorController;

import java.util.Map;

import static core.app.vars.Vars.*;

public class CameraSystem implements AppListener {

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

        cameraControl = new CameraController(cameraSettings);
    }

    @Override
    public void update() {
        cameraControl.update();
    }

    @Override
    public void dispose() {
        cursor.disposeCursor();
    }

}
