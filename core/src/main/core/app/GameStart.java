package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
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
import core.event.GameEvent;
import core.render.GhostOverlayRenderer;
import core.render.OverlayRenderer;
import core.render.PlanRenderer;
import core.render.Renderer;
import core.world.World;
import data.debug.DebugConfig;
import data.debug.DebugType;
import data.map.MapConfig;
import data.map.PresetMap;
import data.map.asset.AssetType;
import data.map.asset.BuildingType;
import ui.InterfaceHandler;

import java.util.Random;

public final class GameStart {

    public static GameContext create() {
        GameContext context = new GameContext();

        //TODO: change every parameter pass into creates to be context.
        context.window = new Window();
        context.window.createGameWindow();

        context.assets = new AssetsHandler();
        context.assets.load();

        context.mapConfig = new MapConfig();
        //TODO: change to better seed system later
        context.mapConfig.seed = (Long) System.currentTimeMillis();
        context.world = World.generateWorld(
            MapConfig.createPresetMap(PresetMap.PLAIN),
            context.assets
        );

        context.viewport = new ExtendViewport(
            context.world.getWorldWidth(),
            context.world.getWorldHeight()
        );

        context.input = new InputHandler();

        context.controller = new Controller(context);
        context.player = new Player(context);

        context.spriteBatch = new SpriteBatch();
        context.shapeRenderer = new ShapeRenderer();
        context.renderer = new Renderer(context);

        context.debug = new Debug(context, DebugConfig.createDefaultConfig());

        context.playerAction = new PlayerAction(context);

        context.overlayRenderer = new OverlayRenderer(context);
        context.ghostOverlayRenderer = new GhostOverlayRenderer(context);

        context.interfaceHandler = new InterfaceHandler(context);

        context.planManager = new PlanManager(context);
        context.planBuilder = new PlanBuilder<>();
        context.planRenderer = new PlanRenderer<>(context);

        context.cameraController = new CameraController(context);
        context.cursorController = new CursorController();
        context.cursorController.loadCursor();

        //multiplexer bla bla...
        InputDesktop.init(context);

        //testers
//        PlanBuilder<AssetType> planBuilderTest1 = new PlanBuilder<>();
//        Random random = new Random();
//        for (int i = 0; i < context.world.getWorldWidth() /2; i++) {
//            for (int j = 0; j < context.world.getWorldHeight() /2; j++) {
//                if (random.nextBoolean()) {
//                    planBuilderTest1.addPlan(i, j, BuildingType.HAZARD_BLOCK);
//                } else {
//                    planBuilderTest1.addPlan(i, j, BuildingType.HAZARD_BLOCK2);
//                }
//            }
//        }
//        GameEvent.PlanBuilderRequest.fire(planBuilderTest1);

//        planManager.addPlanToRenderQueue(planBuilder);

        //debug testers
        context.debug.enableDebugModes(DebugType.CAMERA);

        context.window.setForegroundFPS(0);
        context.window.setVSync(false);


        return context;
    }

}
