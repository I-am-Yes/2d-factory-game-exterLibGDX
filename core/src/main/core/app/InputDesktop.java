package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import core.UiInputGate;
import core.system.context.InterfaceContext;
import core.system.systems.InterfaceSystem;

public final class InputDesktop {
    private InputDesktop() {}

    public static void init() {
        InputMultiplexer multiplexer = new InputMultiplexer();

        //UI pointer blocker
        multiplexer.addProcessor(Vars.uiInputGate);

        multiplexer.addProcessor(Vars.input);

        multiplexer.addProcessor(Vars.uiStage);


        Gdx.input.setInputProcessor(multiplexer);
    }

}
