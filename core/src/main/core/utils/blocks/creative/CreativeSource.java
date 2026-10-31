package core.utils.blocks.creative;

import core.utils.machine.machines.cores.ItemSource;
import core.assets.textures.types.ItemType;

public class CreativeSource extends ItemSource {
    public CreativeSource() {
        super(ItemType.TEST_ITEM, 1, 0.05f, true);
    }
}
