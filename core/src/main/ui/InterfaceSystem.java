package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import arcane.*;
import ui.game.*;
import arcane.input.*;

import static core.app.Vars.*;

public class InterfaceSystem implements ApplicationListener {

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

        uiInputGate = new UiInputGate();
        this.uiHelper = new UiHelper();
        this.UIaction = new InterfaceAction();

        aldrichFont = Style.fonts.Aldrich.getFont();

        textStyle1 = Style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        playerHotbar = new PlayerHotbar(
            Cores.window, Cores.uiStage, skin, UIaction, aldrichFont,
            Cores.input, assets
        );


        gameUI = new GameUI(skin, uiHelper);

        settingsPanel = new SettingsPanel(skin, uiHelper);

        Cores.uiStage.addActor(settingsPanel);

        Events.run(Events.Trigger.tickUpdate,
            () -> gameUI.tickUpdate()

        );

        Events.run(Events.Trigger.update,
            this::UiUpdate
        );

    }

    private void UiUpdate() {
        Cores.uiStage.act(Time.delta());

        gameUI.update();
        playerHotbar.update();

        Cores.uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Cores.viewport.update(width, height, true);
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

    public static void setUiScale(float scale) {
        UiScale = scale;
    }
    public static float getUiScale() {
        return UiScale;
    }

}
