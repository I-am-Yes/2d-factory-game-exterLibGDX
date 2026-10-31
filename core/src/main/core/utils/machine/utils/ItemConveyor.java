package core.utils.machine.utils;

import core.assets.textures.types.ItemType;

public final class ItemConveyor {

    public final ItemType type;

    public float currentX;
    public float currentY;
    public float progress;

    public float visualX;
    public float visualY;
    public float visualSpeed;

    public ItemConveyor(ItemType type) {
        this.type = type;
    }

}
