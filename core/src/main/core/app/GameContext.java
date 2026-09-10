package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.InputHandler;
import core.Player;
import core.Window;
import core.controller.CameraController;
import core.controller.Controller;
import core.controller.CursorController;
import core.controller.PlayerAction;
import core.debug.Debug;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanManager;
import core.render.GhostOverlayRenderer;
import core.render.OverlayRenderer;
import core.render.PlanRenderer;
import core.render.Renderer;
import core.world.World;
import data.map.MapConfig;
import data.map.asset.AssetType;
import ui.InterfaceHandler;

import javax.swing.text.View;
import java.util.Objects;

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
    public OverlayRenderer overlayRenderer;
    public GhostOverlayRenderer ghostOverlayRenderer;

    public Controller controller;
    public CursorController cursorController;
    public CameraController cameraController;

    public Player player;
    public PlayerAction playerAction;

    public PlanManager planManager;
    public PlanBuilder<AssetType> planBuilder;
    public PlanRenderer<AssetType> planRenderer;

    public GameContext() {

    }

    public float getDeltaTime() {
        return Gdx.graphics.getDeltaTime();
    }

}
