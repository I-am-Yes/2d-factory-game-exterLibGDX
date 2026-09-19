package core.machine;

import core.machine.state.Direction;
import core.machine.state.ItemType;
import core.machine.state.MachineType;
import core.machine.utils.Item;

public final class Machine {

    public final int tileX;
    public final int tileY;
    public final MachineType type;
    public final Storage storage;
    public Direction direction;

    public Item item;
    public float productionTimer;

    public Machine(int tileX, int tileY, MachineType type, Direction direction) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.type = type;
        this.direction = direction;
        this.storage =
            type.getStorageSize() > 0 ? new Storage(type.getStorageSize()) : null;
    }

    public boolean hasStorage() {
        return storage != null;
    }

    public boolean acceptItem(ItemType itemType) {
        if (storage == null) {
            return false;
        }
        //TODO:
        // if (!inputWhitelist.contains(itemType)) return false;
        // if (inputBlacklist.contains(itemType)) return false;
        // if (!recipe.accepts(itemType)) return false;

        return storage.acceptItem(itemType);
    }

    public boolean handleItem(ItemType item) {
        return storage != null && storage.handleItem(item);
    }


}
