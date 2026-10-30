package core.utils.unit;

import core.assets.textures.AssetType;

public enum UnitType implements AssetType {
    BOT_UNIT("packed/units", "bot_unit_01", "Bot Unit");

    private final String assetFolder;
    private final String namePNG;
    private final String name;

    UnitType(String assetFolder, String namePNG, String name) {
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
        int lastSlash = assetFolder.lastIndexOf('/');
        return lastSlash >= 0 ? assetFolder.substring(0, lastSlash) : "";
    }

    // Packed textures are loaded through AssetsHandler.getRegion(this).
    @Override
    public String getTexturePath() {
        return assetFolder + "/" + namePNG + ".png";
    }
}
