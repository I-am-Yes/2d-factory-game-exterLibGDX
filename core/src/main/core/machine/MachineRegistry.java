package core.machine;
import com.badlogic.gdx.utils.LongMap;

import static core.system.systems.FactorySystem.tileKey;

public final class MachineRegistry {
    private final LongMap<Machine> machines = new LongMap<>();

    public Machine get(int tileX, int tileY) {
        return machines.get(tileKey(tileX, tileY));
    }

    public void add(Machine machine) {
        machines.put(tileKey(machine.tileX, machine.tileY), machine);
    }

    public Machine remove(int tileX, int tileY) {
        return machines.remove(tileKey(tileX, tileY));
    }
}
