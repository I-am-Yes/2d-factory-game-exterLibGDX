package ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import core.Window;
import core.app.GameContext;
import core.controller.PlayerAction;
import core.world.World;

public class InterfaceHandler {

    private final Window window;
    private final Stage stage;
    private final Style style;
    private final Skin skin;


    private final PlayerHotbar playerHotbar;
    private final GameUI gameUI;

    private final Label.LabelStyle textStyle1;


    public InterfaceHandler(GameContext context) {
        this.window = context.window;

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new FitViewport(1280, 720));
        style = new Style();

        textStyle1 = style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        playerHotbar = new PlayerHotbar(window, stage, skin, style);
        gameUI = new GameUI(window, stage, skin, style);

    }

    public void update() {
        act(window.getDeltaTime());
        gameUI.update(window.getDeltaTime());
        playerHotbar.update();
    }

    public void act(float deltaTime) {
        stage.act(deltaTime);
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
