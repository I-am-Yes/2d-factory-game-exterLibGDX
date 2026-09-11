package core.app.context;

import core.controller.camera.CameraController;
import core.controller.camera.CursorController;

public class CameraContext {

    public CameraController cameraController;
    public CursorController cursor;

    public CameraContext(CameraController cameraController, CursorController cursor) {
        this.cameraController = cameraController;
        this.cursor = cursor;
    }

}
