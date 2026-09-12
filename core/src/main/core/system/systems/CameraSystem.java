package core.system.systems;

import core.system.context.CameraContext;
import core.app.GameContext;
import core.system.ContextProvider;
import core.controller.camera.CameraController;
import core.system.context.PlayerContext;
import core.controller.camera.CursorController;
import core.system.GameSysCycle;

public class CameraSystem implements GameSysCycle, ContextProvider<CameraContext> {

    private GameContext context;
    private PlayerContext playerContext;
    private CameraContext cameraContext;

    private CameraController cameraController;
    private CursorController cursor;

    private float delta;

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

    @Override
    public void update(float delta) {
        this.delta = delta;

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
