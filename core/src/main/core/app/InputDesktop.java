package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import arcane.Cores;
import core.app.vars.Vars;

public final class InputDesktop {
    private InputDesktop() {}

    public static void init() {
        Cores.input.worldInputBlocked = (
            Vars.uiInputGate::isPointerOverGUI
        );

        InputMultiplexer multiplexer = new InputMultiplexer();

        //UI pointer blocker
        multiplexer.addProcessor(Vars.uiInputGate);

        multiplexer.addProcessor(Cores.input);

        multiplexer.addProcessor(Cores.uiStage);


        Gdx.input.setInputProcessor(multiplexer);
    }

}
