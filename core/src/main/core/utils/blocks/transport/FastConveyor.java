package core.utils.blocks.transport;

import core.utils.machine.machines.cores.Conveyor;

public class FastConveyor extends Conveyor {
    public FastConveyor() {
        super(DEFAULT_SPEED * 2f);
    }
}

