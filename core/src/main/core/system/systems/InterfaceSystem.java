package core.system.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import core.UiInputGate;
import core.Window;
import core.app.GameContext;
import core.render.RenderLayer;
import core.system.ContextProvider;
import core.system.GameSysCycle;
import core.system.context.InterfaceContext;
import ui.GameUI;
import ui.InterfaceAction;
import ui.PlayerHotbar;
import ui.Style;

public class InterfaceSystem implements GameSysCycle, ContextProvider<InterfaceContext> {

    private GameContext context;
    private InterfaceContext interfaceContext;

    private final Window window;
    private final Stage stage;
    private final Skin skin;
    private final UiInputGate uiInputGate;
    private final InterfaceAction UIaction;

    private final PlayerHotbar playerHotbar;
    private final GameUI gameUI;

    private float delta;

    private final Label.LabelStyle textStyle1;

    private final BitmapFont aldrichFont;


    public InterfaceSystem(GameContext context) {
        this.context = context;
        this.window = context.window;

        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.stage = new Stage(new FitViewport(window.getWindowWidth(), window.getWindowHeight()));

        this.uiInputGate = new UiInputGate(stage);

        this.UIaction = new InterfaceAction();

        aldrichFont = Style.fonts.Aldrich.getFont();

        textStyle1 = Style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        playerHotbar = new PlayerHotbar(
            window, stage, skin, UIaction, aldrichFont,
            context.input, context.assets
        );

        gameUI = new GameUI(window, stage, skin);

        interfaceContext = new InterfaceContext(
            stage,
            skin,
            uiInputGate,
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

    public UiInputGate getUiInputGate() {
        return this.uiInputGate;
    }

}
