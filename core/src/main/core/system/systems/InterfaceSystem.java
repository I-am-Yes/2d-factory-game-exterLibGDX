package core.system.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.UiInputGate;
import core.app.GameContext;
import core.app.cores.AppListener;
import core.app.cores.GameSysCycle;
import core.app.cores.UpdateDomain;
import core.render.RenderLayer;
import core.system.context.InterfaceContext;
import data.map.asset.AssetType;
import ui.*;
import ui.game.GameUI;
import ui.game.SettingsPanel;

import static core.app.Vars.*;

public class InterfaceSystem implements AppListener, GameSysCycle {

    private GameContext context;
    private InterfaceContext interfaceContext;

    private final Skin skin;
    private final InterfaceAction UIaction;
    private final UiHelper uiHelper;

    private final PlayerHotbar playerHotbar;
    private final GameUI gameUI;
    private final SettingsPanel settingsPanel;

    private float delta;

    private final Label.LabelStyle textStyle1;

    private final BitmapFont aldrichFont;


    //TODO: migrate this to a setting class later
    private static float UiScale = 100 / 100f;


    public InterfaceSystem() {
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        ScreenViewport UiViewPort = new ScreenViewport();
        UiViewPort.setUnitsPerPixel(UiViewPort.getUnitsPerPixel() / getUiScale());
        uiStage = new Stage(UiViewPort);

        uiInputGate = new UiInputGate(uiStage);
        this.uiHelper = new UiHelper();
        this.UIaction = new InterfaceAction();

        aldrichFont = Style.fonts.Aldrich.getFont();

        textStyle1 = Style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        playerHotbar = new PlayerHotbar(
            window, uiStage, skin, UIaction, aldrichFont,
            input, assets
        );

        gameUI = new GameUI(world, window, viewport,
            uiStage, skin, uiHelper, input);

        settingsPanel = new SettingsPanel(skin, uiHelper);

        uiStage.addActor(settingsPanel);

        interfaceContext = new InterfaceContext(
            uiStage,
            skin,
            uiInputGate,
            gameUI,
            playerHotbar,
            settingsPanel
        );
    }

    @Override
    public void update() {
        act();
        gameUI.update();
        playerHotbar.update();
    }


    //TODO: make tick system actually have whitelist
    public void tickUpdate() {

        gameUI.tickUpdate();
    }

    @Override
    public void render() {
        draw();
    }

    public void act() {
        uiStage.act(delta);
    }

    public void draw() {
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        uiStage.dispose();
        skin.dispose();
    }

    @Override
    public RenderLayer renderLayer() {
        return RenderLayer.UI_LAYER_3;
    }

    //game UI should be real time update
    @Override
    public UpdateDomain updateDomain() {
        return UpdateDomain.REAL_TIME;
    }

    @Override
    public boolean updateWhenPaused() {
        return true;
    }

    public AssetType getSelectedType() {
        return playerHotbar.getSelectedType();
    }

    public static void setUiScale(float scale) {
        UiScale = scale;
    }
    public static float getUiScale() {
        return UiScale;
    }

}
