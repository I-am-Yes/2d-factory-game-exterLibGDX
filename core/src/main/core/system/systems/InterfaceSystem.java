package core.system.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import core.Window;
import core.app.GameContext;
import core.render.RenderLayer;
import core.system.ContextProvider;
import core.system.GameSysCycle;
import core.system.context.InterfaceContext;
import ui.GameUI;
import ui.PlayerHotbar;
import ui.Style;

public class InterfaceSystem implements GameSysCycle, ContextProvider<InterfaceContext> {

    private InterfaceContext interfaceContext;

    private final Window window;
    private final Stage stage;
    private final Style style;
    private final Skin skin;


    private final PlayerHotbar playerHotbar;
    private final GameUI gameUI;

    private float delta;

    private final Label.LabelStyle textStyle1;


    public InterfaceSystem(GameContext context) {
        this.window = context.window;

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new FitViewport(1280, 720));
        style = new Style();

        textStyle1 = style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        playerHotbar = new PlayerHotbar(window, stage, skin, style);
        gameUI = new GameUI(window, stage, skin, style);

        interfaceContext = new InterfaceContext(
            stage,
            style,
            skin,
            gameUI,
            playerHotbar
        );
    }

    @Override
    public void update(float delta) {
        this.delta = delta;

        act();
        gameUI.update(delta);
        playerHotbar.update();
    }

    @Override
    public void render() {
        draw();
    }

    public void act() {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public RenderLayer renderLayer() {
        return RenderLayer.UI_LAYER_3;
    }

    @Override
    public InterfaceContext getContext() {
        return interfaceContext;
    }

}
