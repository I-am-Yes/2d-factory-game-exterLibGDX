package core.machine.state;

import data.map.asset.BuildingType;

public enum FactoryType {
    CONVEYOR,
    CREATIVE_SOURCE,
    STORAGE_CHEST,

    ;

    public static FactoryType from(BuildingType type) {
        return switch (type) {
            case CONVEYOR_BELT_2 -> FactoryType.CONVEYOR;
            case CREATIVE_SOURCE -> FactoryType.CREATIVE_SOURCE;
            case STORAGE_CHEST_2 -> FactoryType.STORAGE_CHEST;
            default -> null;
        };
    }

}
