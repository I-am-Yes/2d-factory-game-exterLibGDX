package core.system;

import core.app.GameContext;

/**
 * Abstract class for shared contexts to children who extend this class.
 * This class is used to provide a common context for all game systems.
 * */
public abstract class BaseGameSystem {

    protected final GameContext context;

    protected BaseGameSystem(GameContext context) {
        this.context = context;
    }

}
