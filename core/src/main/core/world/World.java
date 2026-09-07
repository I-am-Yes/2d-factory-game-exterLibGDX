package core.world;

import Data.map.asset.FloorType;
import Data.map.MapConfig;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import core.AssetsHandler;
import core.event.GameEvent;
import core.map.MapGenerator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class World {

    private static final String FLOOR_LAYER_NAME = "Floor Layer";
    private static final String BUILDING_LAYER_NAME = "Building Layer";
    private static final String GHOST_LAYER_NAME = "Ghost Layer";

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;

    // tile map layer variables
    private final TiledMapTileLayer floorLayer;
    private final TiledMapTileLayer buildingLayer;
    private final TiledMapTileLayer ghostLayer;

    // grid for tile map layers
    private final FloorType[][] floorGrid;
    private final FloorType[][] buildingGrid;
    private final FloorType[][] ghostGrid;

    private final int tilesWidth;
    private final int tilesHeight;

    private final float tileSize;
    private final float worldWidth;
    private final float worldHeight;


    private final long seed;


    public World(
        TiledMap map, MapConfig mapConfig,
        //pass layers in
        TiledMapTileLayer floorLayer,
        TiledMapTileLayer buildingLayer,
        TiledMapTileLayer ghostLayer,
        //pass grids in
        FloorType[][] floorGrid,
        FloorType[][] buildingGrid,
        FloorType[][] ghostGrid
    ) {

        this.map = map;
        this.seed = mapConfig.seed;

        this.floorLayer = floorLayer;
        this.buildingLayer = buildingLayer;
        this.ghostLayer = ghostLayer;

        this.floorGrid = floorGrid;
        this.buildingGrid = buildingGrid;
        this.ghostGrid = ghostGrid;

        this.tilesWidth = mapConfig.width;
        this.tilesHeight = mapConfig.height;

        this.tileSize = 1f;
        this.worldWidth = tilesWidth * tileSize;
        this.worldHeight = tilesHeight * tileSize;

        float unitScale = 1f / mapConfig.getTilePixel();
        this.mapRenderer = new OrthogonalTiledMapRenderer(map,  unitScale);

    }

    public static World generateWorld(MapConfig mapConfig, AssetsHandler assets) {
        TiledMap map = new TiledMap();

        map.getProperties().put("width", mapConfig.width);
        map.getProperties().put("height", mapConfig.height);
        map.getProperties().put("tilewidth", assets.getTileWidth());
        map.getProperties().put("tileheight", assets.getTileHeight());

        map.getTileSets().addTileSet(assets.buildTileSet());

        //create layers
        TiledMapTileLayer floorLayer = new TiledMapTileLayer(
            mapConfig.width, mapConfig.height,
            assets.getTileWidth(), assets.getTileHeight()
        );
        TiledMapTileLayer buildingLayer = new TiledMapTileLayer(
            mapConfig.width, mapConfig.height,
            assets.getTileWidth(), assets.getTileHeight()
        );
        TiledMapTileLayer ghostLayer = new TiledMapTileLayer(
            mapConfig.width, mapConfig.height,
            assets.getTileWidth(), assets.getTileHeight()
        );

        //set layer name
        floorLayer.setName(FLOOR_LAYER_NAME);
        buildingLayer.setName(BUILDING_LAYER_NAME);
        ghostLayer.setName(GHOST_LAYER_NAME);

        //add layer to map
        map.getLayers().add(floorLayer);
        map.getLayers().add(buildingLayer);
        map.getLayers().add(ghostLayer);

        //generate layer grid
        FloorType[][] floorGrid = MapGenerator.generateTiledMap(mapConfig);
        FloorType[][] buildingGrid = new FloorType[mapConfig.width][mapConfig.height];
        FloorType[][] ghostGrid = new FloorType[mapConfig.width][mapConfig.height];

        MapGenerator.applyToLayer(floorLayer, floorGrid, assets);


        GameEvent.MapGenerated.fire(mapConfig, floorGrid);

        return new World(map, mapConfig,
            floorLayer, buildingLayer, ghostLayer,
            floorGrid, buildingGrid, ghostGrid
        );
    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void dispose() {
        mapRenderer.dispose();
        map.dispose();
    }

    public FloorType getBuildingAt(int tileX, int tileY) {
        return buildingGrid[tileX][tileY];
    }

    public boolean hasBuildingAt(int tileX, int tileY) {
        return getBuildingAt(tileX, tileY) != null;
    }

    public boolean canPlaceBuildingAt(int tileX, int tileY) {
        return isWalkable(tileX, tileY)
            && isInBounds(tileX, tileY)
            && !hasBuildingAt(tileX, tileY);
    }

    public boolean placeBuildingAt(int tileX, int tileY, FloorType floorType, AssetsHandler assets) {
        if (!canPlaceBuildingAt(tileX, tileY)) return false;
        //buildingGrid[tileX][tileY] = ;
        return true;
    }

    public FloorType getGhostTileAt(int tileX, int tileY) {
        return ghostGrid[tileX][tileY];
    }

    public FloorType[][] getFloorGrid() {
        return floorGrid;
    }


    public boolean isInBounds(int tileX, int tileY) {
        return tileX >= 0 && tileX < tilesWidth && tileY >= 0 && tileY < tilesHeight;
    }

    public boolean hasTile(int tileX, int tileY) {
        if (!isInBounds(tileX, tileY)) return false;
        return floorLayer.getCell(tileX, tileY) != null;
    }

    public FloorType getFloorAt(int tileX, int tileY) {
        if (!isInBounds(tileX, tileY)) return null;
        return floorGrid[tileX][tileY];
    }

    public FloorType getFloorAt(float tileX, float tileY) {
        return getFloorAt((int)tileX, (int)tileY);
    }

    public FloorType getFloorAt(Vector2 tile) {
        return getFloorAt((int)tile.x, (int)tile.y);
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

    public boolean placeFloor(int tileX, int tileY, FloorType floorType, AssetsHandler assets) {
        if (!isInBounds(tileX, tileY)) return false;
        if (floorType == FloorType.WATER) return false;

        floorGrid[tileX][tileY] = floorType;

        TiledMapTile tile = assets.getTile(floorType);
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(tile);
        floorLayer.setCell(tileX, tileY, cell);

        return true;
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
