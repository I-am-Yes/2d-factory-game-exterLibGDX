package data.map.asset;

public enum BuildingType implements AssetType {
    HAZARD_BLOCK("packed/tiles/industrial", "hazard block", "Iron Hazard Block"),
    HAZARD_BLOCK2("packed/tiles/industrial", "hazard block2", "Copper Hazard Block"),


    CONVEYOR_BELT("packed/machines/conveyors", "conveyor_belt", "Conveyor Belt"),
    CONVEYOR_BELT_2("packed/machines/conveyors", "conveyor_belt_2", "Conveyor Belt 2"),

    CREATIVE_SOURCE("packed/machines/storages", "storage_chest", "Creative Source"),
    STORAGE_CHEST_2("packed/machines/storages", "storage_chest_2", "Storage Chest 2"),


    ;

    private final String assetFolder;
    private final String namePNG;
    private final String name;

    BuildingType(String assetFolder, String namePNG, String name) {
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
