package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.assets.AssetsHandler;
import core.InputHandler;
import core.Window;
import core.system.ContextProvider;
import core.controller.camera.CameraController;
import core.controller.camera.CursorController;
import core.system.GameSysCycle;
import core.world.World;
import data.map.MapConfig;

public final class GameContext {

    public MapConfig mapConfig;

    public Window window;
    public World world;
    public Viewport viewport;

    public SpriteBatch renderSpriteBatch;
    public ShapeRenderer shapeRenderer;

    public AssetsHandler assets;
    public InputHandler input;

    public CursorController cursorController;
    public CameraController cameraController;

    public GameSystem systems;

    public GameTest gameTest;

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

    public SpriteBatch getBatch() {
        return renderSpriteBatch;
    }

}
