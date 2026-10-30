package core.utils.item.items.tools;

import core.assets.textures.types.ItemType;
import core.utils.item.types.usable.Tools;

import static core.app.Vars.*;

public class SpawnBotItem extends Tools {

    public SpawnBotItem() {
        texture = ItemType.SPAWN_BOT_ITEM;
    }

    @Override
    public void use(float x, float y) {
        if (!active || !usable) return;
        botManager.spawnBot(x, y, 90f);
    }
}
