package core.app;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import core.Window;
import core.app.cores.AppListener;
import core.app.cores.GameCore;
import core.app.extras.test.GameTest;
import core.assets.AssetsHandler;
import core.InputHandler;
import core.system.systems.*;
import core.world.World;
import data.map.MapConfig;
import data.map.PresetMap;

import static core.app.Vars.*;

public class GameStart extends GameCore {

    public void createLoadingContext() {

        window = new Window();
        window.init();
        window.setVSync(false);
        window.setForegroundFPS(0);

        assets = new AssetsHandler();
        assets.queueLoad();
    }

    public void finishGameStart() {

        batch = new SpriteBatch();
        shape = new ShapeRenderer();

        mapConfig = MapConfig.createPresetMap(PresetMap.PLAIN);
        //TODO: change to better seed system later
        mapConfig.seed = System.currentTimeMillis();
        world = World.generateWorld(mapConfig, assets);

        viewport = new ExtendViewport(
            world.getWorldWidth(),
            world.getWorldHeight()
        );

        input = new InputHandler();


        add(new InterfaceSystem());
        add(new RenderSystem());
        add(new PlayerSystem());
        add(new CameraSystem());
        add(new EntrySystem());
        add(new FactorySystem());
        add(new DebugSystem());


        //multiplexer bla bla...
        InputDesktop.init();

        gameTest = new GameTest();

    }

    public void add(AppListener child) {
        super.add(child);
    }
}
