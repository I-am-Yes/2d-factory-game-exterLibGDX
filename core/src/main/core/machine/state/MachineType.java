package core.machine.state;

import data.map.asset.BuildingType;

public enum MachineType {
    CONVEYOR(1, 0),
    CREATIVE_SOURCE(1, 0),
    STORAGE_CHEST(1, 1000),

    ;

    private final float size;
    private final int storageSize;

    MachineType(float size, int storageSize) {
        this.size = size;
        this.storageSize = storageSize;
    }

    public static MachineType from(BuildingType type) {
        return switch (type) {
            case CONVEYOR_BELT_2 -> MachineType.CONVEYOR;
            case CREATIVE_SOURCE -> MachineType.CREATIVE_SOURCE;
            case STORAGE_CHEST_2 -> MachineType.STORAGE_CHEST;
            default -> null;
        };
    }

    public final float getSize() {
        return size;
    }

    public final int getStorageSize() {
        return storageSize;
    }
}
