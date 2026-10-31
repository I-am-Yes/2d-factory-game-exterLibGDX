package core.items.tools;

import core.items.ItemTexture;
import core.items.types.usable.Tools;
import core.machine.state.ItemType;

import static core.app.Vars.*;

@ItemTexture(ItemType.SPAWN_BOT_ITEM)
public class SpawnBotItem extends Tools {
    @Override
    public void use(float x, float y) {
        if (!active || !usable) return;
        botManager.spawnBot(x, y, 90f);
    }
}
