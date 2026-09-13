package core.system.context;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.UiInputGate;
import ui.GameUI;
import ui.PlayerHotbar;
import ui.Style;

public class InterfaceContext {

    public Stage stage;
    public Skin skin;

    public UiInputGate uiInputGate;

    public GameUI gameUI;
    public PlayerHotbar playerHotbar;

    public InterfaceContext(Stage stage, Skin skin, UiInputGate uiInputGate, GameUI gameUI, PlayerHotbar playerHotbar) {
        this.stage = stage;
        this.skin = skin;

        this.uiInputGate = uiInputGate;

        this.gameUI = gameUI;
        this.playerHotbar = playerHotbar;
    }

}
