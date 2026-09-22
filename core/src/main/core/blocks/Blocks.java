package core.blocks;

import core.machine.Block;
import core.machine.machines.CreativeSource;
import core.machine.machines.NormalChest;
import core.machine.machines.cores.Conveyor;
import core.machine.machines.FastConveyor;

public class Blocks {
    public static Block

    normalConveyor, fastConveyor,
    creativeSource, normalChest

    ;

    public static void load() {
        normalConveyor = new Conveyor();
        fastConveyor = new FastConveyor();

        normalChest = new NormalChest();

        creativeSource = new CreativeSource();
    }

}
