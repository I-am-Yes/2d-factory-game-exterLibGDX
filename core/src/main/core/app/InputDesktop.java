package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import core.system.systems.InterfaceSystem;

public final class InputDesktop {
    private InputDesktop() {}

    public static void init(GameContext context) {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(context.input);
        multiplexer.addProcessor(context.getSystem(InterfaceSystem.class).getStage());
        Gdx.input.setInputProcessor(multiplexer);
    }

}
