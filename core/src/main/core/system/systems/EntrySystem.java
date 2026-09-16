package core.system.systems;

import com.badlogic.gdx.Input;
import core.InputHandler;
import core.Window;
import core.app.GameContext;
import core.system.ContextProvider;
import core.system.GameSysCycle;
import core.system.context.EntryContext;
import core.system.context.InterfaceContext;
import core.utils.ScreenshotCapture;

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
            toggleSettingPanel();
        }

        if (input.isKeyJustPressed(Input.Keys.F3)) {
            toggleDebugPanel();
        }
        if (input.isKeyJustPressed(Input.Keys.F11)) {
            //window.setWindowFullscreen();
            window.setBorderlessFullscreen();
        }

        if (!input.isKeyPressed(Input.Keys.SHIFT_LEFT) && input.isKeyJustPressed(Input.Keys.F12)) {
            ScreenshotCapture.requestCapture();
        }

        if (input.isKeyPressed(Input.Keys.SHIFT_LEFT) && input.isKeyJustPressed(Input.Keys.F12)) {
            ScreenshotCapture.captureMapArea(
                context.world,
                (int) -context.world.getWorldWidth(),
                (int) -context.world.getWorldHeight(),
                (int) context.world.getWorldWidth(),
                (int) context.world.getWorldHeight(),
                4
            );
        }

    }

    private void toggleSettingPanel() {
        interfaceContext.settingsPanel.togglePanel();
    }

    private void toggleDebugPanel() {
        interfaceContext.gameUI.setDebugInfoVisible(!interfaceContext.gameUI.isDebugInfoVisible());
    }

    @Override
    public boolean updateWhenPaused() {
        return true;
    }

    @Override
    public EntryContext getContext() {
        return entryContext;
    }
}
