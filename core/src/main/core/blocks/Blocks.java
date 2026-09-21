package core.blocks;

import core.machine.Block;
import core.machine.Conveyor;
import core.machine.Machine;
import core.machine.machines.FastConveyor;

public class Blocks {
    public static Block

    normalConveyor, fastConveyor, creativeSource

    ;

    public static void load() {
        normalConveyor = new Conveyor();
        fastConveyor = new FastConveyor();
    }

}
