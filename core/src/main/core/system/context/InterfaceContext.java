package core.system.context;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import ui.GameUI;
import ui.PlayerHotbar;
import ui.Style;

public class InterfaceContext {

    public Stage stage;
    public Style style;
    public Skin skin;

    public GameUI gameUI;
    public PlayerHotbar playerHotbar;

    public InterfaceContext(Stage stage, Style style, Skin skin, GameUI gameUI, PlayerHotbar playerHotbar) {
        this.stage = stage;
        this.style = style;
        this.skin = skin;

        this.gameUI = gameUI;
        this.playerHotbar = playerHotbar;
    }

}
