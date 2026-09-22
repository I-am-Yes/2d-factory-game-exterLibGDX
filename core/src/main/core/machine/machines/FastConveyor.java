package core.machine.machines;

import core.machine.machines.cores.Conveyor;

public class FastConveyor extends Conveyor {
    public FastConveyor() {
        super(DEFAULT_SPEED * 2f);
    }
}

