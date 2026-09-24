package core.system.context;

import core.machine.ItemManager;
import core.machine.render.ItemRender;

public class FactoryContext {

    public ItemManager itemManager;
    public ItemRender itemRender;

    public FactoryContext(ItemManager itemManager, ItemRender itemRender) {
        this.itemManager = itemManager;
        this.itemRender = itemRender;
    }

}
