package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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
import data.debug.DebugConfig;
import data.map.MapConfig;
import data.map.PresetMap;
import ui.InterfaceHandler;

public final class GameStart {

    public static GameContext create() {
        GameContext context = new GameContext();

        //TODO: change every parameter pass into creates to be context.
        context.debug = new Debug(context, DebugConfig.createDefaultConfig());
        context.window = new Window();
        context.window.createGameWindow();

        context.assets = new AssetsHandler();
        context.assets.load();

        context.world = World.generateWorld(MapConfig.createPresetMap(PresetMap.PLAIN), context.assets);
        context.viewport = new ExtendViewport(context.world.getWorldWidth(), context.world.getWorldHeight());

        context.input = new InputHandler();
        context.interfaceHandler = new InterfaceHandler(context.window, context.world, context.playerAction);


        //temporary player texture
        //TODO: add texture register for this
        Texture playerTexture = new Texture("unpacked/player/player.png");
        context.player = new Player(playerTexture, context.world, context.world.getWorldWidth(), context.world.getWorldHeight(), context.controller, context.assets);

        context.spriteBatch = new SpriteBatch();
        context.shapeRenderer = new ShapeRenderer();
        context.renderer = new Renderer(context);

        context.playerAction = new PlayerAction(context);

        context.overlayRenderer = new OverlayRenderer(context);
        context.ghostOverlayRenderer = new GhostOverlayRenderer(context);

        context.planManager = new PlanManager(context.world);
        context.planBuilder = new PlanBuilder<>();
        context.planRenderer = new PlanRenderer<>(context);


        context.controller = new Controller(context.window, context.input);
        context.cursorController = new CursorController();
        context.cameraController = new CameraController(context);



        return context;
    }

}
