package core.system.context;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.UiInputGate;
import ui.game.GameUI;
import ui.PlayerHotbar;
import ui.game.SettingsPanel;

public class InterfaceContext {

    public Stage stage;
    public Skin skin;

    public UiInputGate uiInputGate;

    public GameUI gameUI;
    public PlayerHotbar playerHotbar;
    public SettingsPanel settingsPanel;

    public InterfaceContext(Stage stage, Skin skin, UiInputGate uiInputGate, GameUI gameUI, PlayerHotbar playerHotbar, SettingsPanel settingsPanel) {
        this.stage = stage;
        this.skin = skin;

        this.uiInputGate = uiInputGate;

        this.gameUI = gameUI;
        this.playerHotbar = playerHotbar;
        this.settingsPanel = settingsPanel;
    }

}
