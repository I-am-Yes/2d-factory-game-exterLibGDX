package data.map.asset;

public enum FloorType implements AssetType {
    SAND("packed/tiles/tiles/Sand", "sand_middle", "Sand"),
    ROCK("packed/tiles/tiles/Rock", "rock_middle", "Rock"),
    STONE("packed/tiles/tiles/Stone", "stone_middle", "Stone"),
    MARBLE("packed/tiles/tiles/Marble", "marble_middle", "Marble"),
    DIRT("packed/tiles/tiles/Dirt", "dirt_middle", "Dirt"),
    GRASS("packed/tiles/tiles/Grass", "grass_middle", "Grass"),
    WATER("packed/tiles/tiles/Water", "water_middle", "Water");

    private final String assetFolder;
    private final String namePNG;
    private final String name;

    FloorType(String assetFolder, String namePNG, String name) {
        this.assetFolder = assetFolder;
        this.namePNG = namePNG;
        this.name = name;
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
    public String getName() {
        return name;
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

    public static FloorType fromId(String id) {
        return valueOf(id.toUpperCase());
    }
}
