package core.app;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import core.Window;
import core.app.cores.GameCore;
import core.app.extras.LoadingScreen;
import core.assets.AssetsHandler;
import core.world.World;
import data.map.MapConfig;

public class Vars {

    ////hmm
    public static final int MAX_FACTORY_COUNT = Integer.MAX_VALUE;

    public static GameCore core;

    public static Window window;
    public static AssetsHandler assets;
    public static SpriteBatch batch;
    public static ShapeRenderer shape;
    public static MapConfig mapConfig;
    public static World world;

    public static LoadingScreen loadingScreen;

    public static void init() {
        loadingScreen = LoadingScreen.init();

        window = new Window();
        assets = new AssetsHandler();
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
    }


}
