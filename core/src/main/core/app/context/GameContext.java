package core.app.context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.InputHandler;
import core.app.GameSystems;
import core.Window;
import core.controller.camera.CameraController;
import core.player.mechanic.PlayerController;
import core.controller.camera.CursorController;
import core.debug.Debug;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanManager;
import core.render.PlanRenderer;
import core.render.Renderer;
import core.system.CameraSystem;
import core.system.GameSystem;
import core.system.PlayerSystem;
import core.world.World;
import data.map.MapConfig;
import data.map.asset.AssetType;
import ui.InterfaceHandler;

public final class GameContext {

    public MapConfig mapConfig;

    public Debug debug;
    public Window window;
    public World world;
    public Viewport viewport;

    public AssetsHandler assets;
    public InputHandler input;
    public InterfaceHandler interfaceHandler;

    public Renderer renderer;
    public SpriteBatch spriteBatch;
    public ShapeRenderer shapeRenderer;

    public PlayerController controller;
    public CursorController cursorController;
    public CameraController cameraController;

    public PlanManager planManager;
    public PlanBuilder<AssetType> planBuilder;
    public PlanRenderer<AssetType> planRenderer;

    public GameSystems systems;
    public GameSystem gameSystem;
    public PlayerSystem playerSystem;
    public CameraSystem cameraSystem;

    public PlayerContext playerContext;
    public CameraContext cameraContext;

    public GameContext() {}

    public float getDeltaTime() {
        return Gdx.graphics.getDeltaTime();
    }

}
