package core.machine;

import core.machine.state.ItemType;

public final class Storage {

    private final int capacity;
    private final int[] storage;
    private int totalItems;

    public Storage(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Storage capacity must be positive."
            );
        }

        this.capacity = capacity;
        this.storage = new int[ItemType.values().length];
    }

    public boolean acceptItem(ItemType item) {
        return item != null && !isFull();
    }

    public boolean handleItem(ItemType item) {
        if (!acceptItem(item)) {
            return false;
        }
        storage[item.ordinal()]++;
        totalItems++;
        return true;
    }

    public int getStorageIndex(int index) {
        return ItemType.values()[index].ordinal();
    }

    public int getStorageItem(ItemType item) {
        if (item == null) return  0;
        return storage[item.ordinal()];
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return totalItems >= capacity;
    }
}
