package core.app;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.UiInputGate;
import core.Window;
import core.app.cores.GameCore;
import core.app.extras.LoadingScreen;
import core.app.extras.test.GameTest;
import core.assets.AssetsHandler;
import core.controller.camera.CameraController;
import core.player.Player;
import core.world.World;
import data.map.MapConfig;

public class Vars {

    ////hmm
    public static final int MAX_FACTORY_COUNT = Integer.MAX_VALUE;

    public static GameCore core;
    public static GameStart start;

    public static Window window;
    public static Viewport viewport;
    public static OrthographicCamera camera;
    public static AssetsHandler assets;
    public static SpriteBatch batch;
    public static ShapeRenderer shape;
    public static MapConfig mapConfig;
    public static World world;
    public static InputHandler input;

    public static UiInputGate uiInputGate;
    public static Stage uiStage;

    public static LoadingScreen loadingScreen;
    public static GameTest gameTest;

    public static Player player;
    //TODO: temporary using this, later change it
    public static CameraController cameraController;

    public static void init() {
        loadingScreen = LoadingScreen.init();

        start = new GameStart();

        camera = (OrthographicCamera) viewport.getCamera();
    }


}
