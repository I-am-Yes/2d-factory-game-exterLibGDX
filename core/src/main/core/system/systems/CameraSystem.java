package core.system.systems;

import core.app.context.CameraContext;
import core.app.GameContext;
import core.app.context.ContextProvider;
import core.controller.camera.CameraController;
import core.app.context.PlayerContext;
import core.controller.camera.CursorController;
import core.system.GameSystem;

public class CameraSystem implements GameSystem, ContextProvider<CameraContext> {

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

        this.cameraController = new CameraController(context, playerContext);

        this.cameraContext = new CameraContext(
            cameraController,
            cursor
        );
    }
    public void update(float delta) {
        cameraController.update(delta);
    }

    public CameraContext getCameraContext() {
        return cameraContext;
    }

    @Override
    public CameraContext getContext() {
        return cameraContext;
    }
}
