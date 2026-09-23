package core.machine;

import core.machine.state.Direction;
import core.machine.state.ItemType;
import core.machine.state.MachineType;
import core.machine.utils.Item;

import java.util.List;

public class Machine {
    private MachineGroup group;

    public final int tileX;
    public final int tileY;
    public final MachineType type;

    //TODO: implement accept input/out black/white list
    public List<Object> blackList;
    public List<Object> whiteList;

    public Direction direction;
    public float timeEfficient = 1f;

    //storage definitions
    public final int[] storage;
    public final int capacity;
    public int totalItems;

    public Item item;

    public float itemInputSpeed = 2f;
    public float itemOutputSpeed = 2f;

    public boolean smoothVisual = true;
    public final float VISUAL_SMOOTHNESS = 20f;

    boolean active;
    int activeIndex = -1;

    public Machine(int tileX, int tileY, Direction direction, MachineType type) {
        this(tileX, tileY, direction, type, type.getStorageSize());
    }

    public Machine(int tileX, int tileY, Direction direction, MachineType type, int storageSize) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.direction = direction;
        this.type = type;
        this.capacity = storageSize;
        this.storage = storageSize == 0 ? null : new int[ItemType.values().length];
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

    protected final Machine getMachineAt(int tileX, int tileY) {
        if (group == null) {
            return null;
        }

        return group.getMachineAt(tileX, tileY);
    }



    //TODO: implement onDestroyed() to handle machine destruction logic
    public void onDestroyed() {}

    /***
     * Returns the effective delta time, taking into account the machine's time efficiency.
     * @param delta The base delta time.
     * @return The effective delta time.
     */
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

    public boolean hasOutputLoad() {
        return item != null;
    }

    public boolean canPushLoad() {
        //default to no
        return false;
    }

    //TODO: add whiteList/blackList of things can speed up using eDelta, and implement that logic
    // eDelta also affect normal delta when enable.
    public boolean canSpeedUp() {
        return false;
    }

    public void updateItemVisual(float delta) {
        // Override this method to update visual elements of the machine.
        item.progress += delta;
        item.currentX = item.progress;
        item.currentY = item.progress;
        item.visualX = item.progress;
        item.visualY = item.progress;
    }

    public void updateItemSmoothVisual(float visualSmoothness) {
        // Override this method to update smooth visual elements of the machine.
        item.progress += visualSmoothness;
        item.currentX = item.progress;
        item.currentY = item.progress;
        item.visualX = item.progress;
        item.visualY = item.progress;
    }

    public final boolean pushLoadTo(Machine target) {
        if (target == null || !canPushLoad()) {
            return false;
        }
        Item output = item;
        output.visualSpeed = getOutputSpeed();
        if (!target.acceptLoad(output)) {
            return false;
        }
        item = null;
        return true;
    }

    protected final boolean holdOutputLoad(Item load) {
        if (load == null || item != null) {
            return false;
        }
        item = load;
        return true;
    }
    /**
     * @param load place the item to the output gate of the current machine.
     */
    protected final void placeLoadAtOutput(Item load) {
        float size = type.getSize();
        float centerX = tileX + size * 0.5f;
        float centerY = tileY + size * 0.5f;

        load.currentX = centerX + direction.dx * size * 0.5f;
        load.currentY = centerY + direction.dy * size * 0.5f;
//        load.currentX = centerX;
//        load.currentY = centerY;

        load.visualX = load.currentX;
        load.visualY = load.currentY;
        load.progress = 0f;
    }

    //TODO:
    // if (!inputWhitelist.contains(itemType)) return false;
    // if (inputBlacklist.contains(itemType)) return false;
    // if (!recipe.accepts(itemType)) return false;
    public boolean acceptItem(ItemType itemType) {
        return itemType != null
            && storage != null
            && !isFull();
    }

    public boolean handleItem(ItemType itemType) {
        if (!acceptItem(itemType)) {
            return false;
        }

        storage[itemType.ordinal()]++;
        totalItems++;
        return true;
    }

    public boolean acceptLoad(Item load) {
        return canAcceptLoad(load)
            && handleItem(load.type);
    }

    public boolean canAcceptLoad(Item load) {
        return load != null && capacity > 0 && !isFull() && acceptItem(load.type);
    }

    public int getStorageItem(ItemType itemType) {
        if (itemType == null || storage == null) {
            return 0;
        }

        return storage[itemType.ordinal()];
    }

    public float getInputSpeed() {
        return itemInputSpeed;
    }

    public float getOutputSpeed() {
        return itemOutputSpeed;
    }

    public int[] getStorage() {
        return storage;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        if (storage == null) return true;
        return totalItems >= capacity;
    }

    public boolean hasStorage() {
        return storage != null;
    }

    public void enableSmooth() {
        this.smoothVisual = true;
    }

    public void disableSmooth() {
        this.smoothVisual = false;
    }
}
