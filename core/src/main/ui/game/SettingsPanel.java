package ui.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import ui.InterfaceCreate;
import ui.Style;

import java.time.format.TextStyle;

public final class SettingsPanel {

    private final Window settingsPanel;
    private final Stage stage;
    private final Skin skin;

    private final InterfaceCreate uiCreate;

    public SettingsPanel(Stage stage, Skin skin, InterfaceCreate interfaceCreate) {
        this.stage = stage;
        this.skin = skin;
        this.uiCreate = interfaceCreate;

        Label.LabelStyle textStyle1 = Style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        this.settingsPanel = uiCreate.createPanel("SETTINGS", skin, 300f, 500f, 20f);

        uiCreate.addCloseButton(settingsPanel, skin);

        settingsPanel.add(uiCreate.createTextLabel("Settings content goes here", textStyle1)).left().pad(10f).row();

        settingsPanel.add(uiCreate.createTextLabel("Settings content goes here2", textStyle1)).left().pad(10f).row();
        settingsPanel.add(uiCreate.createTextLabel("Settings content goes here3", textStyle1)).left().pad(10f).row();
        settingsPanel.add(uiCreate.createTextLabel("Settings content goes here4", textStyle1)).left().pad(10f).row();
        settingsPanel.add(uiCreate.createTextLabel("Settings content goes here5", textStyle1)).left().pad(10f).row();


        //TODO: add options to settings panel
        //TODO: add options to pause game on settings panel open

        float centerX = (stage.getViewport().getWorldWidth() - settingsPanel.getWidth()) / 2f;
        float centerY = (stage.getViewport().getWorldHeight() - settingsPanel.getHeight()) / 2f;
        settingsPanel.setPosition(centerX, centerY);

        close();

        stage.addActor(settingsPanel);
    }

    public void open() {
        settingsPanel.setVisible(true);
    }

    public void close() {
        settingsPanel.setVisible(false);
    }

    public boolean isPanelOpen() {
        return settingsPanel.isVisible();
    }

    public Window getPanel() {
        return settingsPanel;
    }
}
