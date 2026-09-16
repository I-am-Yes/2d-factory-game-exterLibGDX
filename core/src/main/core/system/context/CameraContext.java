package core.system.context;

import core.config.camera.CameraSettings;
import core.controller.camera.CameraController;
import core.controller.camera.CursorController;

public class CameraContext {

    public CameraController cameraController;
    public CursorController cursor;

    public CameraSettings cameraSettings;

    public CameraContext(CameraController cameraController, CameraSettings cameraSettings, CursorController cursor) {
        this.cameraController = cameraController;
        this.cameraSettings = cameraSettings;
        this.cursor = cursor;
    }

}
