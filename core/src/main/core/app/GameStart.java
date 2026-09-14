package core.app;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import core.AssetsHandler;
import core.InputHandler;
import core.Window;
import core.system.systems.*;
import core.world.World;
import data.debug.DebugType;
import data.map.MapConfig;
import data.map.PresetMap;

public final class GameStart {

    public static GameContext create() {
        GameContext context = new GameContext();

        //TODO: change every parameter pass into creates to be context.
        context.window = new Window();
        context.window.createGameWindow();

        context.window.setVSync(true);

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

        context.systems = new GameSystem();

        context.addSystem(new InterfaceSystem(context));
        context.addSystem(new PlayerSystem(context));
        context.addSystem(new RenderSystem(context));
        context.addSystem(new CameraSystem(context, context.getSystemContext(PlayerSystem.class)));

        context.addSystem(new EntrySystem(context, context.input));

        context.addSystem(new DebugSystem(context));

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
        context.getSystem(DebugSystem.class).getDebug().enableDebugModes(
            DebugType.CAMERA
        );

        context.window.setForegroundFPS(0);

        return context;
    }

}
