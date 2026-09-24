package core.assets;

public enum AtlasType {
    TILES_ATLAS("packed/tiles/tiles/tile atlas.atlas"),
    MACHINE_ATLAS("packed/machines/machine atlas.atlas"),
    ITEM_ATLAS("packed/items/item atlas.atlas");

    private final String path;

    AtlasType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getFolderPath() {
        if (path.contains("/")) {
            return path.substring(0, path.lastIndexOf('/'));
        }
        return "";
    }

    public static AtlasType fromAssetFolder(String folder) {
        String normalized = folder.replace('\\', '/');

        for (AtlasType type : values()) {
            if (normalized.startsWith(type.getFolderPath())) {
                return type;
            }
        }

        throw new IllegalStateException(
            "No atlas configured for asset folder: " + folder
        );
    }
}
