package core.utils.blocks;

import core.utils.Registry;
import core.assets.textures.types.*;
import core.utils.blocks.creative.*;
import core.utils.blocks.transport.*;
import core.utils.machine.machines.cores.*;
import core.utils.blocks.definition.Block;

public class Blocks {
    private static final Registry<BuildingType, Block> definitions =
        new Registry<>("block definition");
    public static Block

    normalConveyor, fastConveyor,
    creativeSource, normalChest

    ;

    public static void load() {
        definitions.clear();

        normalConveyor = register(BuildingType.CONVEYOR_BELT, new Conveyor());
        fastConveyor = register(BuildingType.CONVEYOR_BELT_2, new FastConveyor());

        normalChest = register(BuildingType.STORAGE_CHEST_2, new NormalChest());

        creativeSource = register(BuildingType.CREATIVE_SOURCE, new CreativeSource());
    }

    public static <T extends Block> T register(BuildingType type, T definition) {
        return definitions.register(type, definition);
    }

    public static Block get(BuildingType type) {
        return definitions.get(type);
    }
//    public static Block get(Block block) {
//        return definitions.get(block.texture);
//    }

}
