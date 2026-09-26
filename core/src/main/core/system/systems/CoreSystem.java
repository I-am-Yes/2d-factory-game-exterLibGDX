package core.system.systems;

import core.system.ContextProvider;
import core.system.context.CoreContext;
import core.app.cores.GameSysCycle;

public class CoreSystem implements GameSysCycle, ContextProvider<CoreContext> {

    private CoreContext coreContext;

    @Override
    public CoreContext getContext() {
        return coreContext;
    }
}
