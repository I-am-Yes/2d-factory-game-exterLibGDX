package core.machine.state;

import data.map.asset.AssetType;

public enum ItemType implements AssetType {
    TEST_ITEM("packed/items/test/" , "stone_bar_item", "Test Item"),


    ;
    private final String assetFolder;
    private final String namePNG;
    private final String name;

    ItemType(String assetFolder, String namePNG, String name) {
        this.assetFolder = assetFolder;
        this.namePNG = namePNG;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getAssetFolder() {
        return assetFolder;
    }

    public String getNamePNG() {
        return namePNG;
    }

    public String getTextureParentFolder() {
        String folder = getAssetFolder();
        if (folder.contains("/")) {
            return folder.substring(0, folder.lastIndexOf('/'));
        }
        return "";
    }

    public String getTexturePath() {
        return getAssetFolder() + "/" + getNamePNG() + ".png";
    }

}
