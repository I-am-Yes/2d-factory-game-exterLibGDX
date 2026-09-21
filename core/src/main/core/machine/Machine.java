package core.machine;

import core.machine.state.Direction;
import core.machine.state.ItemType;
import core.machine.state.MachineType;
import core.machine.utils.Item;

public class Machine {
    private MachineGroup group;

    public final int tileX;
    public final int tileY;
    public final MachineType type;
    public final Storage storage;

    public Direction direction;
    public float timeEfficient = 1f;
    public float storageSize;

    public Item item;
    public float productionTimer;

    boolean active;
    int activeIndex = -1;

    public Machine(int tileX, int tileY, Direction direction, MachineType type) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.direction = direction;
        this.type = type;
        this.storage =
            type.getStorageSize() > 0 ? new Storage(type.getStorageSize()) : null;
    }

    public Machine(int tileX, int tileY, Direction direction, MachineType type, int storageSize) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.direction = direction;
        this.type = type;
        this.storage = new Storage(storageSize);
    }

    final void attachTo(MachineGroup group) {
        if (this.group != null && this.group != group) {
            throw new IllegalStateException(
                "Machine already belongs to another group"
            );
        }
        this.group = group;
    }

    final void detachFrom(MachineGroup group) {
        if (this.group == group) this.group = null;
    }

    protected final void wake() {
        if (this.group == null) {
            throw new IllegalStateException(
                "Machine is not registered"
            );
        }
        this.group.activate(this);
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

    public boolean canAcceptLoad(Item load) {
        return load != null && acceptItem(load.type);
    }

    public boolean acceptLoad(Item load) {
        if (!canAcceptLoad(load)) {
            return false;
        }

        return handleItem(load.type);
    }

    //TODO: this
    public void onDestroyed() {

    }

        public float eDelta(float delta) {
        return timeEfficient * delta;
    }

    public void setTimeEfficient(float timeEfficient) {
        this.timeEfficient = timeEfficient;
    }

    public boolean isActive() {
        return active;
    }

    public boolean shouldStayActive() {
        return false;
    }
}
