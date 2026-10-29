package core.items.types.usable;

import core.items.ItemBase;

public abstract class UsableItem extends ItemBase {
    public abstract void use(float x, float y);

    public boolean active = false;
    public boolean usable = true;

    public float useSpeed;
    public float duration;

}
