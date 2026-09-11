package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import core.app.context.GameContext;

public final class InputDesktop {
    private InputDesktop() {}

    public static void init(GameContext context) {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(context.input);
        multiplexer.addProcessor(context.interfaceHandler.getStage());
        Gdx.input.setInputProcessor(multiplexer);
    }

}
