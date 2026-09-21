package core.machine.machines;

import core.machine.Conveyor;

public class FastConveyor extends Conveyor {
    public FastConveyor() {
        super(DEFAULT_SPEED * 2f);
    }
}

