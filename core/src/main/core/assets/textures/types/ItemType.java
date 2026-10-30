package core.assets.textures.types;

import core.assets.textures.AssetType;

public enum ItemType implements AssetType {
    TEST_ITEM("packed/items/test/", "stone_bar_item", "Test Item"),
    SPAWN_BOT_ITEM("packed/items/test/", "spawn_bot_item", "Spawn Bot Item");

    private final String assetFolder;
    private final String namePNG;
    private final String name;

    ItemType(String assetFolder, String namePNG, String name) {
        this.assetFolder = assetFolder;
        this.namePNG = namePNG;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAssetFolder() {
        return assetFolder;
    }

    @Override
    public String getNamePNG() {
        return namePNG;
    }

    @Override
    public String getTextureParentFolder() {
        String folder = getAssetFolder();
        if (folder.contains("/")) {
            return folder.substring(0, folder.lastIndexOf('/'));
        }
        return "";
    }

    @Override
    public String getTexturePath() {
        return getAssetFolder() + "/" + getNamePNG() + ".png";
    }
}
