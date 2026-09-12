package core.app;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import core.system.ContextProvider;
import core.render.RenderLayer;
import core.system.GameSysCycle;

public final class GameSystem {

    private final Array<GameSysCycle> systems = new Array<>();
    private final ObjectMap<Class<?>, GameSysCycle> systemType = new ObjectMap<>();
    private final ObjectMap<Class<?>, Object> contexts = new ObjectMap<>();

    /** register a new game system
     @param <T> the type of the game system
     @param system the game system to register
     @return the registered game system
     * */
    public <T extends GameSysCycle> T add(T system) {
        Class<?> systemClass = system.getClass();

        if (systemType.containsKey(systemClass)) {
            throw new RuntimeException(
                "System already registered: " + systemClass.getSimpleName()
            );
        }

        //registers system's context
        if (system instanceof ContextProvider<?> provider) {
            Class<?> contextClass = provider.getContext().getClass();
            if (contexts.containsKey(contextClass)) {
                throw new IllegalStateException(
                    "Context already registered: " + contextClass.getSimpleName()
                );
            }
            contexts.put(contextClass, provider.getContext());
        }

        systemType.put(systemClass, system);
        systems.add(system);
        return system;
    }

    /** get a game system by system. Class
     @param systemClass the class of the game system to get
     @param <T> the type of the game system
     @return the game system of the specified type
     */
    public <T extends GameSysCycle> T get(Class<T> systemClass) {
        GameSysCycle system = systemType.get(systemClass);
        if (system == null) {
            throw new IllegalStateException(
                "System not registered: " + systemClass.getSimpleName()
            );
        }
        return systemClass.cast(system);
    }

    /** get a context by context. Class
     * @param contextClass the class of the context to get
     * @return the context of the specified type
     * @param <T> the type of the context
     */
    public <T> T getContext(Class<T> contextClass) {
        Object systemContext = contexts.get(contextClass);
        if (systemContext == null) {
            throw  new IllegalStateException(
                "Context not registered: " + contextClass.getSimpleName()
            );
        }
        return contextClass.cast(systemContext);
    }

    public void update(float delta) {
        for (int i = 0; i < systems.size; i++) {
            systems.get(i).update(delta);
        }
    }

    public void render() {
        for (RenderLayer layer : RenderLayer.values()) {
            for (int i = 0; i < systems.size; i++) {
                GameSysCycle system = systems.get(i);

                if (system.renderLayer() == layer) {
                    system.render();
                }
            }
        }
    }

    public void resize(int width, int height) {
        for (int i = 0; i < systems.size; i++) {
            systems.get(i).resize(width, height);
        }
    }

    public void dispose() {
        for (int i = systems.size - 1; i >= 0; i--) {
            systems.get(i).dispose();
        }
    }

}
