package core;

import Data.map.FloorType;
import Data.map.MapConfig;
import com.badlogic.gdx.maps.Map;
import core.event.GameEvent;
import core.map.MapGenerator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class World {

    private static final String FLOOR_LAYER_NAME = "Floor";

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final TiledMapTileLayer tileLayer;

    private final int tilesWidth;
    private final int tilesHeight;

    private final float tileSize;
    private final float worldWidth;
    private final float worldHeight;

    private final long seed;

    private final FloorType[][] floorGrid;

    public World(TiledMap map, TiledMapTileLayer tileLayer, MapConfig mapConfig, FloorType[][] floorGrid) {

        this.map = map;
        this.seed = mapConfig.seed;

        this.tileLayer = tileLayer;
        this.floorGrid = floorGrid;

        this.tilesWidth = mapConfig.width;
        this.tilesHeight = mapConfig.height;

        this.tileSize = 1f;
        this.worldWidth = tilesWidth * tileSize;
        this.worldHeight = tilesHeight * tileSize;

        float unitScale = 1f / mapConfig.getTilePixel();
        this.mapRenderer = new OrthogonalTiledMapRenderer(map,  unitScale);

    }

    public static World generateWorld(MapConfig mapConfig, BlockAssets assets) {
        TiledMap map = new TiledMap();

        map.getProperties().put("width", mapConfig.width);
        map.getProperties().put("height", mapConfig.height);
        map.getProperties().put("tilewidth", assets.getTileWidth());
        map.getProperties().put("tileheight", assets.getTileHeight());

        map.getTileSets().addTileSet(assets.buildTileSet());

        TiledMapTileLayer floorLayer = new TiledMapTileLayer(
            mapConfig.width,
            mapConfig.height,
            assets.getTileWidth(),
            assets.getTileHeight()
        );

        floorLayer.setName(FLOOR_LAYER_NAME);
        map.getLayers().add(floorLayer);

        FloorType[][] grid = MapGenerator.generateTiledMap(mapConfig);
        MapGenerator.applyToLayer(floorLayer, grid, assets);


        GameEvent.MapGenerated.fire(mapConfig, grid);

        return new World(map, floorLayer, mapConfig, grid);
    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void dispose() {
        mapRenderer.dispose();
        map.dispose();
    }

    public FloorType[][] getFloorGrid() {
        return floorGrid;
    }


    public boolean isInBounds(int tileX, int tileY) {
        return tileX >= 0 && tileX < tilesWidth && tileY >= 0 && tileY < tilesHeight;
    }

    public boolean hasTile(int tileX, int tileY) {
        if (!isInBounds(tileX, tileY)) return false;
        return tileLayer.getCell(tileX, tileY) != null;
    }

    public FloorType getFloorAt(int tileX, int tileY) {
        if (!isInBounds(tileX, tileY)) return null;
        return floorGrid[tileX][tileY];
    }

    public FloorType getFloorName(int tileX, int tileY) {
        if (!isInBounds(tileX, tileY)) return null;
        //TODO: get floor name at x, y.
        return floorGrid[tileX][tileY];
    }

    public boolean isWalkable(int tileX, int tileY) {
        FloorType floor = getFloorAt(tileX, tileY);
        return floor != null && floor != FloorType.WATER;
    }

    public int worldToTileX(int worldX) {
        return (int) (worldX / tileSize);
    }
    public int worldToTileY(int worldY) {
        return (int) (worldY / tileSize);
    }

    public float getTileSize() {
        return tileSize;
    }
    public float getTilesWidth() {
        return tilesWidth;
    }
    public float getTilesHeight() {
        return tilesHeight;
    }
    public float getWorldWidth() {
        return worldWidth;
    }
    public float getWorldHeight() {
        return worldHeight;
    }

}
