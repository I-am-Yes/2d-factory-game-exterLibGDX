package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.UiInputGate;
import core.app.cores.AppListener;
import core.app.cores.GameSysCycle;
import core.app.cores.Time;
import core.app.cores.UpdateDomain;
import core.app.vars.Cores;
import core.render.RenderLayer;
import data.map.asset.AssetType;
import ui.game.GameUI;
import ui.game.SettingsPanel;

import static core.app.vars.Vars.*;

public class InterfaceSystem implements AppListener, GameSysCycle {

    private final Skin skin;
    private final InterfaceAction UIaction;
    private final UiHelper uiHelper;


    private final Label.LabelStyle textStyle1;

    private final BitmapFont aldrichFont;

    public static GameUI gameUI;
    public static PlayerHotbar playerHotbar;
    public static SettingsPanel settingsPanel;


    //TODO: migrate this to a setting class later
    private static float UiScale = 100 / 100f;


    public InterfaceSystem() {
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        ScreenViewport UiViewPort = new ScreenViewport();
        UiViewPort.setUnitsPerPixel(UiViewPort.getUnitsPerPixel() / getUiScale());
        Cores.uiStage = new Stage(UiViewPort);

        uiInputGate = new UiInputGate(Cores.uiStage);
        this.uiHelper = new UiHelper();
        this.UIaction = new InterfaceAction();

        aldrichFont = Style.fonts.Aldrich.getFont();

        textStyle1 = Style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        playerHotbar = new PlayerHotbar(
            Cores.window, Cores.uiStage, skin, UIaction, aldrichFont,
            Cores.input, Cores.assets
        );

        gameUI = new GameUI(world, Cores.window, Cores.viewport,
            Cores.uiStage, skin, uiHelper, Cores.input);

        settingsPanel = new SettingsPanel(skin, uiHelper);

        Cores.uiStage.addActor(settingsPanel);

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
        Cores.uiStage.act(Time.delta());
    }

    public void draw() {
        Cores.uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Cores.uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        Cores.uiStage.dispose();
        skin.dispose();
    }

    public void openSettings() {
        settingsPanel.open();
    }
    public void closeSettings() {
        settingsPanel.close();
    }
    public void toggleSettings() {
        settingsPanel.toggle();
    }

    public void toggleDebug() {
        gameUI.setDebugInfoVisible(!gameUI.isDebugInfoVisible());
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
