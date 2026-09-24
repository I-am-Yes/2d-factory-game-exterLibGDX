package core.machine.machines;

import core.machine.machines.cores.ItemSource;
import core.machine.state.ItemType;

public class CreativeSource extends ItemSource {
    public CreativeSource() {
        super(ItemType.TEST_ITEM, 1, 0.05f, true);
    }
}
