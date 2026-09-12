package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.InputHandler;
import core.Window;
import core.system.ContextProvider;
import core.controller.camera.CameraController;
import core.controller.camera.CursorController;
import core.render.Renderer;
import core.system.GameSysCycle;
import core.world.World;
import data.map.MapConfig;
import core.system.systems.InterfaceSystem;

public final class GameContext {

    public MapConfig mapConfig;

    public Window window;
    public World world;
    public Viewport viewport;

    public AssetsHandler assets;
    public InputHandler input;

    public CursorController cursorController;
    public CameraController cameraController;

    public GameSystem systems;

    public GameContext() {}

    public <T extends GameSysCycle> T addSystem(T system) {
        return systems.add(system);
    }

    public <T extends GameSysCycle> T getSystem(Class<T> systemClass) {
        return systems.get(systemClass);
    }

    public <T> T getContext(Class<T> contextClass) {
        return systems.getContext(contextClass);
    }

    public <T, S extends GameSysCycle & ContextProvider<T>> T getSystemContext(Class<S> systemClass) {
        return getSystem(systemClass).getContext();
    }

    public float getDeltaTime() {
        return Gdx.graphics.getDeltaTime();
    }

}
