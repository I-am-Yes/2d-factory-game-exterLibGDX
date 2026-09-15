package ui.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import ui.UiHelper;
import ui.Style;

public final class SettingsPanel extends Window {

    public SettingsPanel(Skin skin, UiHelper uiHelper) {
        super("SETTINGS", skin);

        Label.LabelStyle textStyle1 = Style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        uiHelper.configsPanel(this, skin, 300f, 500f, 20f);


        uiHelper.addCloseButton(this, skin);

        add(uiHelper.createTextLabel("Settings content goes here", textStyle1)).left().pad(10f).row();

        add(uiHelper.createTextLabel("Settings content goes here2", textStyle1)).left().pad(10f).row();
        add(uiHelper.createTextLabel("Settings content goes here3", textStyle1)).left().pad(10f).row();
        add(uiHelper.createTextLabel("Settings content goes here4", textStyle1)).left().pad(10f).row();
        add(uiHelper.createTextLabel("Settings content goes here5", textStyle1)).left().pad(10f).row();


        //TODO: add options to settings panel
        //TODO: add options to pause game on settings panel open


    }

    public void open() {
        setVisible(true);
    }
    public void close() {
        setVisible(false);
    }
    public boolean isPanelOpen() {
        return isVisible();
    }

}
