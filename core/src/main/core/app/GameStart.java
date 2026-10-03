package core.app;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import arcane.*;
import arcane.graphics.Window;
import arcane.ApplicationListener;
import arcane.input.InputHandler;
import core.app.cores.Update;
import core.assets.AssetsHandler;
import core.render.PlanRenderer;
import core.system.systems.*;
import core.world.World;
import data.map.MapConfig;
import data.map.PresetMap;
import ui.InterfaceSystem;

import static core.app.vars.Vars.*;

public class GameStart extends GameCore {

    public void startGame() {

        Cores.window = new Window();
        Cores.window.init();
        Cores.window.setVSync(false);
        Cores.window.setForegroundFPS(0);

        assets = new AssetsHandler();
        assets.queueLoad();
    }

    public void finishGameStart() {

        Cores.batch = new SpriteBatch();
        Cores.shape = new ShapeRenderer();

        mapConfig = MapConfig.createPresetMap(PresetMap.PLAIN);
        //TODO: change to better seed system later
        mapConfig.seed = System.currentTimeMillis();
        world = World.generateWorld(mapConfig, assets);

        Cores.viewport = new ExtendViewport(
            world.getWorldWidth(),
            world.getWorldHeight()
        );

        Cores.camera = (OrthographicCamera) Cores.viewport.getCamera();

        Cores.input = new InputHandler();

        add(world);
        add(new PlanRenderer());

        add(new Update());
        add(new PlayerSystem());
        add(new EntrySystem());
        add(new FactorySystem());

        add(ui = new InterfaceSystem());

        add(new DebugSystem());

        //multiplexer bla bla...
        InputDesktop.init();

    }

    public void add(ApplicationListener child) {
        super.add(child);
    }
}
