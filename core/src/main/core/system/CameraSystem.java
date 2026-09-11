package core.system;

import core.app.context.CameraContext;
import core.app.context.GameContext;
import core.controller.camera.CameraController;
import core.app.context.PlayerContext;
import core.controller.camera.CursorController;

public class CameraSystem implements GameSystem {

    private GameContext context;
    private PlayerContext playerContext;
    private CameraContext cameraContext;

    private CameraController cameraController;
    private CursorController cursor;

    public CameraSystem(GameContext context, PlayerContext playerContext) {
        this.context = context;
        this.playerContext = playerContext;

        this.cursor = new CursorController();
        cursor.loadCursor();

        this.cameraController = new CameraController(context, context.playerContext);

        this.cameraContext = new CameraContext(cameraController, cursor);
        context.cameraContext = getCameraContext();
    }
    public void update(float delta) {
        cameraController.update(delta);
    }

    public CameraContext getCameraContext() {
        return cameraContext;
    }

}
