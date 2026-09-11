package core.app;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import core.system.GameSystem;

public final class GameSystems {

    private final Array<GameSystem> systems = new Array<>();
    private final ObjectMap<Class<?>, GameSystem> systemType = new ObjectMap<>();

    /** register a new game system
     @param <T> the type of the game system
     @param system the game system to register
     @return the registered game system
     * */
    public <T extends GameSystem> T add(T system) {
        Class<?> systemClass = system.getClass();

        if (systemType.containsKey(systemClass)) {
            throw new RuntimeException(
                "System already registered: " + systemClass.getSimpleName()
            );
        }

        systemType.put(systemClass, system);
        systems.add(system);
        return system;
    }

    /** get a game system by type
     @param systemClass the class of the game system to get
     @param <T> the type of the game system
     @return the game system of the specified type
     */
    public <T extends GameSystem> T get(Class<T> systemClass) {
        GameSystem system = systemType.get(systemClass);
        if (system == null) {
            throw new IllegalStateException(
                "System not registered: " + systemClass.getSimpleName()
            );
        }
        return systemClass.cast(system);
    }

    public void update(float delta) {
        for (GameSystem system : systems) {
            system.update(delta);
        }
    }

    public void render() {
        for (GameSystem system : systems) {
            system.render();
        }
    }

    public void resize(int width, int height) {
        for (GameSystem system : systems) {
            system.resize(width, height);
        }
    }

    public void dispose() {
        for (int i = systems.size - 1; i >= 0; i--) {
            systems.get(i).dispose();
        }
    }

}
