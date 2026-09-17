package core.machine.utils;

import core.machine.state.ItemType;

public final class Item {

    public final ItemType type;

    //previous pos to calculate animation between each update tick
    public float previousX;
    public float previousY;
    public float currentX;
    public float currentY;
    public float progress;

    public Item(ItemType type) {
        this.type = type;
    }

}
