package core.utils.item;

import core.assets.textures.types.ItemType;
import core.utils.Registry;
import core.utils.item.items.test.TestBar;
import core.utils.item.items.tools.SpawnBotItem;

public final class Items {
    private static final Registry<ItemType, Item> definitions =
        new Registry<>("item definition");
    public static Item

        testItem, spawnBotItem

    ;

    public static void load() {
        definitions.clear();

        testItem = register(ItemType.TEST_ITEM, new TestBar());
        spawnBotItem = register(ItemType.SPAWN_BOT_ITEM, new SpawnBotItem());

    }

    public static <T extends Item> T register(ItemType type, T definition) {
        return definitions.register(type, definition);
    }

    public static Item get(ItemType type) {
        return definitions.get(type);
    }
    public static Item get(Item item) {
        return definitions.get(item.texture);
    }
}
