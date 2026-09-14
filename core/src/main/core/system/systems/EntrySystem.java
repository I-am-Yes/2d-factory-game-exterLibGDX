package core.system.systems;

import com.badlogic.gdx.Input;
import core.InputHandler;
import core.Window;
import core.app.GameContext;
import core.system.ContextProvider;
import core.system.GameSysCycle;
import core.system.context.EntryContext;
import core.system.context.InterfaceContext;

public class EntrySystem implements GameSysCycle, ContextProvider<EntryContext> {

    private final GameContext context;
    private final EntryContext entryContext;
    private final InterfaceContext interfaceContext;

    private final InputHandler input;
    private final Window window;

    public EntrySystem(GameContext context, InputHandler input) {
        this.context = context;
        this.interfaceContext = context.getContext(InterfaceContext.class);
        this.input = input;
        this.window = context.window;



        this.entryContext = new EntryContext(

        );
    }

    @Override
    public void update(float delta) {



        if (input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            openSettingPanel();
        }

        if (input.isKeyJustPressed(Input.Keys.F11)) {
            //window.setWindowFullscreen();
            window.setBorderlessFullscreen();
        }

    }

    private void openSettingPanel() {
        if (!interfaceContext.settingsPanel.isPanelOpen()) {
            interfaceContext.settingsPanel.open();
        } else {
            interfaceContext.settingsPanel.close();
        }
    }

    @Override
    public EntryContext getContext() {
        return entryContext;
    }
}
