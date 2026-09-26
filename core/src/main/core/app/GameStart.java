package core.app;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import core.app.cores.GameSystem;
import core.app.extras.test.GameTest;
import core.assets.AssetsHandler;
import core.InputHandler;
import core.event.events.AppEvent;
import core.system.systems.*;
import core.world.World;
import data.map.MapConfig;
import data.map.PresetMap;

import static core.app.Vars.*;

public final class GameStart extends AppEvent {

    public static void createLoadingContext() {
        GameContext context = new GameContext();

        window.init();
        window.setVSync(false);
        window.setForegroundFPS(0);

        assets = new AssetsHandler();
        assets.queueLoad();
    }

    public static void finishGameStart(GameContext context) {

        context.renderSpriteBatch = new SpriteBatch();
        context.shapeRenderer = new ShapeRenderer();

        context.mapConfig = MapConfig.createPresetMap(PresetMap.PLAIN);
        //TODO: change to better seed system later
        context.mapConfig.seed = System.currentTimeMillis();
        context.world = World.generateWorld(
            context.mapConfig,
            context.assets
        );

        context.viewport = new ExtendViewport(
            context.world.getWorldWidth(),
            context.world.getWorldHeight()
        );

        context.input = new InputHandler();

        context.systems = new GameSystem();

        context.addSystem(new InterfaceSystem(context));
        context.addSystem(new RenderSystem(context));
        context.addSystem(new PlayerSystem(context));
        context.addSystem(new CameraSystem(context, context.getSystemContext(PlayerSystem.class)));

        context.addSystem(new EntrySystem(context, context.input));

        context.addSystem(new FactorySystem(context));

        context.addSystem(new DebugSystem(context));

        //multiplexer bla bla...
        InputDesktop.init(context);

        //debug testers
        context.getSystem(DebugSystem.class).getDebug().enableDebugModes(

        );



        context.gameTest = new GameTest(context);

    }

}
