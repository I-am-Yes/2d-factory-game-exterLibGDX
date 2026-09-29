package core.app.vars;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.Window;
import core.app.GameStart;
import core.app.extras.LoadingScreen;
import core.app.extras.test.GameTest;
import core.assets.AssetsHandler;

public class Cores {

    public static GameStart start;

    public static Window window;
    public static Viewport viewport;
    public static InputHandler input;
    public static OrthographicCamera camera;

    public static AssetsHandler assets;
    public static SpriteBatch batch;
    public static ShapeRenderer shape;
    public static Stage uiStage;

    public static LoadingScreen loadingScreen;

    public static GameTest gameTest;

}
