package core.utils.item.items.tools;

import core.assets.textures.types.ItemType;
import core.utils.item.types.usable.Tools;
import core.utils.unit.units.SmallBot;

import static core.app.Vars.botManager;

public class SpawnSmallBotItem extends Tools {

    public SpawnSmallBotItem() {
        texture = ItemType.SPAWN_BOT_ITEM;
    }

    @Override
    public void use(float x, float y) {
        if (!active || !usable) return;
        botManager.spawnUnit(new SmallBot(), x, y, 90f);
    }
}
