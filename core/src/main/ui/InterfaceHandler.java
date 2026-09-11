package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import core.Window;
import core.app.context.GameContext;

public class InterfaceHandler {

    private final Window window;
    private final Stage stage;
    private final Style style;
    private final Skin skin;


    private final PlayerHotbar playerHotbar;
    private final GameUI gameUI;

    private float delta;

    private final Label.LabelStyle textStyle1;


    public InterfaceHandler(GameContext context) {
        this.window = context.window;

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new FitViewport(1280, 720));
        style = new Style();

        textStyle1 = style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        playerHotbar = new PlayerHotbar(window, stage, skin, style);
        gameUI = new GameUI(window, stage, skin, style);

        this.delta = context.getDeltaTime();

    }

    public void update(float delta) {
        this.delta = delta;

        act();
        gameUI.update(delta);
        playerHotbar.update();
    }

    public void act() {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public void resize() {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public Stage getStage() {
        return stage;
    }

}
