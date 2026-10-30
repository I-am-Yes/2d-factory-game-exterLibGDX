package core.utils.item.types.usable;

import core.utils.item.Item;

public abstract class UsableItem extends Item {
    public abstract void use(float x, float y);

    public boolean active = false;
    public boolean usable = true;

    public float useSpeed;
    public float duration;

}
