package core.utils.item.items.test;

import core.assets.textures.types.ItemType;
import core.utils.item.Item;
import core.utils.item.types.Stackable;

public class TestBar extends Item implements Stackable {
    public int currentStack = 0;

    public TestBar() {
        texture = ItemType.TEST_ITEM;
    }

    @Override
    public int getMaxStack() {
        return 16;
    }

    @Override
    public int getCurrentStack() {
        return currentStack;
    }

    @Override
    public void setCurrentStack(int amount) {
        currentStack = amount;
    }
}
