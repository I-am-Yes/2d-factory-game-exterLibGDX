package Data.map.asset;

public enum BuildingType implements AssetType {
    HAZARD_BLOCK("blocks/industrial", "hazard block"),
    HAZARD_BLOCK2("blocks/industrial", "hazard block2");

    private final String assetFolder;
    private final String namePNG;

    BuildingType(String assetFolder, String namePNG) {
        this.assetFolder = assetFolder;
        this.namePNG = namePNG;
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
    public String getTexturePath() {
        return getAssetFolder() + "/" + getNamePNG() + ".png";
    }
}
