package core.assets;

import com.badlogic.gdx.assets.AssetManager;
import core.app.Vars;
import core.event.events.AppEvent;
import data.map.asset.BuildingType;
import data.map.asset.AssetType;
import data.map.asset.FloorType;
import data.map.MapConfig;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class AssetsHandler implements Disposable {

    private static final MapConfig mapConfig = new MapConfig();
    private static final int TILE_PIXEL = mapConfig.getTilePixel();
    private final AssetManager assetManager = new AssetManager();

    private final ObjectMap<AtlasType, TextureAtlas> atlases = new ObjectMap<>();
    private final ObjectMap<AssetType, TextureRegion> regions = new ObjectMap<>();
    private final ObjectMap<AssetType, TiledMapTile> tiledTiles = new ObjectMap<>();

    private void requestAtlas(AtlasType atlasType) {
        String path = atlasType.getPath();

        if (!assetManager.isLoaded(path) && !assetManager.contains(path)) {
            assetManager.load(path, TextureAtlas.class);
        }
    }

    private TextureAtlas requireAtlas(AtlasType atlasType) {
        TextureAtlas cached = atlases.get(atlasType);
        if (cached != null) {
            return cached;
        }
        String path = atlasType.getPath();
        if (!assetManager.isLoaded(path)) {
            requestAtlas(atlasType);

            // Blocks only on the first request for this atlas.
            assetManager.finishLoadingAsset(path);
        }

        TextureAtlas atlas = assetManager.get(path, TextureAtlas.class);
        atlases.put(atlasType, atlas);
        return atlas;
    }

    public void queueLoad() {
        for (AtlasType atlas : AtlasType.values()) {
            assetManager.load(atlas.getPath(), TextureAtlas.class);
        }
    }

    public boolean updateLoading() {
        if (!assetManager.update()) {
            AppEvent.LoadingScreenEvent.fire(getProgress());
            return false;
        }

        for (AtlasType atlas : AtlasType.values()) {
            String path = atlas.getPath();
            if (!assetManager.isLoaded(path) && !assetManager.contains(path)) {
                atlases.put(atlas, assetManager.get(path, TextureAtlas.class));
            }
        }

        return true;
    }

    public float getProgress() {
        return assetManager.getProgress();
    }

    public TextureRegion getRegion(AssetType type) {
        TextureRegion cached = regions.get(type);

        if (cached != null) {
            return cached;
        }

        AtlasType atlasType =
            AtlasType.fromAssetFolder(type.getAssetFolder());

        TextureAtlas atlas = requireAtlas(atlasType);

        if (atlas == null) {
            throw new IllegalStateException(
                "Atlas has not been loaded: " + atlasType
            );
        }

        String regionName = getAtlasRegionName(type);
        TextureRegion region = atlas.findRegion(regionName);

        if (region == null) {
            throw new IllegalStateException(
                "Missing region '" + regionName
                    + "' in atlas " + atlasType
            );
        }

        region.getTexture().setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Nearest
        );

        regions.put(type, region);
        return region;
    }


    public TiledMapTileSet buildTileSet() {
        TiledMapTileSet tileSet = new TiledMapTileSet();
        tileSet.setName("tiles");

        int tileWidth = getTileWidth();
        int tileHeight = getTileHeight();

        tileSet.getProperties().put("firstgid", 1);
        tileSet.getProperties().put("tilewidth", tileWidth);
        tileSet.getProperties().put("tileheight", tileHeight);

        for (FloorType type : FloorType.values()) {
            TiledMapTile tile = getTile(type);
            tileSet.putTile(tile.getId(), tile);
        }

        return tileSet;
    }

    public int getTileWidth() {
        return TILE_PIXEL;
    }

    public int getTileHeight() {
        return TILE_PIXEL;
    }

    public int getTextureWidth(AssetType type) {
        TiledMapTile tile = getTile(type);
        if (tile == null) return 0;
        return tile.getTextureRegion().getRegionWidth();
    }

    public int getTextureHeight(AssetType type) {
        TiledMapTile tile = getTile(type);
        if (tile == null) return 0;
        return tile.getTextureRegion().getRegionHeight();
    }

    public Vector2 getTextureSize(AssetType type) {
        TiledMapTile tile = getTile(type);
        if (tile == null || tile.getTextureRegion() == null) return new Vector2(0 ,0);
        return new Vector2(getTextureWidth(type), getTextureHeight(type));
    }

    private int nextTileId = 1;

    public TiledMapTile getTile(AssetType type) {
        if (type == null) return null;

        TiledMapTile cached = tiledTiles.get(type);

        if (cached != null) return cached;

        TiledMapTile tile =
            new StaticTiledMapTile(getRegion(type));

        tile.setId(nextTileId++);
        tiledTiles.put(type, tile);

        return tile;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        atlases.clear();
        regions.clear();
        tiledTiles.clear();
    }

    public void updateUnloadAtlas() {
        //TODO: later add auto unload unused atlas to save memory, not needed for now.
    }

    public void unloadAtlas(AtlasType type) {
        String path = type.getPath();

        if (assetManager.isLoaded(path)) {
            assetManager.unload(path);
        }

        atlases.remove(type);

        // Also remove cached regions and tiles belonging to this atlas.
        for (AssetType asset : regions.keys().toArray()) {
            if (AtlasType.fromAssetFolder(asset.getAssetFolder()) == type) {
                regions.remove(asset);
                tiledTiles.remove(asset);
            }
        }
    }

    private String getAtlasRegionName(AssetType type) {
        String folder = type.getAssetFolder().replace('\\', '/');

        while (folder.endsWith("/")) {
            folder = folder.substring(0, folder.length() - 1);
        }

        int lastSlash = folder.lastIndexOf('/');
        String category = folder.substring(lastSlash + 1);

        return category + "/" + type.getNamePNG();
    }

    public TextureAtlas getAtlas(AtlasType atlasType) {
        TextureAtlas tilesAtlas = atlases.get(atlasType);
        if (tilesAtlas == null) {
            throw new IllegalStateException(
                "Atlas has not been loaded: " + atlasType
            );
        }
        return tilesAtlas;
    }


}
