package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import core.UiInputGate;
import core.system.context.InterfaceContext;
import core.system.systems.InterfaceSystem;

public final class InputDesktop {
    private InputDesktop() {}

    public static void init(GameContext context) {

        UiInputGate uiInputGate = context.getContext(InterfaceContext.class).uiInputGate;
        InterfaceSystem interfaceSystem = context.getSystem(InterfaceSystem.class);

        InputMultiplexer multiplexer = new InputMultiplexer();

        //UI pointer blocker
        multiplexer.addProcessor(uiInputGate);

        multiplexer.addProcessor(context.input);

        multiplexer.addProcessor(interfaceSystem.getStage());




        Gdx.input.setInputProcessor(multiplexer);
    }

}
