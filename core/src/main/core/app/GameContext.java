package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.InputHandler;
import core.Window;
import core.app.context.ContextProvider;
import core.controller.camera.CameraController;
import core.player.mechanic.PlayerController;
import core.controller.camera.CursorController;
import core.debug.Debug;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanManager;
import core.render.PlanRenderer;
import core.render.Renderer;
import core.system.GameSystem;
import core.world.World;
import data.map.MapConfig;
import data.map.asset.AssetType;
import ui.InterfaceHandler;

public final class GameContext {

    public MapConfig mapConfig;

    public Window window;
    public World world;
    public Viewport viewport;

    public AssetsHandler assets;
    public InputHandler input;
    public InterfaceHandler interfaceHandler;

    public Renderer renderer;

    public CursorController cursorController;
    public CameraController cameraController;

    public GameSystems systems;

    public GameContext() {}

    public <T extends GameSystem> T addSystem(T system) {
        return systems.add(system);
    }

    public <T extends GameSystem> T getSystem(Class<T> systemClass) {
        return systems.get(systemClass);
    }

    public <T> T getContext(Class<T> contextClass) {
        return systems.getContext(contextClass);
    }

    public <T, S extends GameSystem & ContextProvider<T>> T getSystemContext(Class<S> systemClass) {
        return getSystem(systemClass).getContext();
    }

    public float getDeltaTime() {
        return Gdx.graphics.getDeltaTime();
    }

}
