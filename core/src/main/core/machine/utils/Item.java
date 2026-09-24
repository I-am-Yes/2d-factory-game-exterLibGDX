package core.machine.utils;

import core.machine.state.ItemType;

public final class Item {

    public final ItemType type;

    public float currentX;
    public float currentY;
    public float progress;

    public float visualX;
    public float visualY;
    public float visualSpeed;

    public Item(ItemType type) {
        this.type = type;
    }

}
