package core.blocks;

import com.badlogic.gdx.utils.ObjectMap;
import core.machine.machines.definition.Block;
import core.blocks.creative.CreativeSource;
import core.blocks.transport.NormalChest;
import core.machine.machines.cores.Conveyor;
import core.blocks.transport.FastConveyor;
import data.map.asset.BuildingType;

public class Blocks {
    private static final ObjectMap<BuildingType, Block> definitions = new ObjectMap<>();
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

    private static <T extends Block> T register(BuildingType type, T definition) {
        if (definitions.containsKey(type)) {
            throw new IllegalStateException(
                "Duplicate block definition: " + type
            );
        }

        definitions.put(type, definition);
        return definition;
    }

    public static Block get(BuildingType type) {
        return definitions.get(type);
    }

}
