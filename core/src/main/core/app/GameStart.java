package core.app;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import core.assets.AssetsHandler;
import core.InputHandler;
import core.Window;
import core.system.systems.*;
import core.world.World;
import data.map.MapConfig;
import data.map.PresetMap;

public final class GameStart {

    public static GameContext createLoadingContext() {
        GameContext context = new GameContext();

        context.window = new Window();
        context.window.createGameWindow();
        context.window.setVSync(false);
        context.window.setForegroundFPS(0);

        context.assets = new AssetsHandler();
        context.assets.queueLoad();

        return context;
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
