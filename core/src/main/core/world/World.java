package core.world;

import data.map.asset.BuildingType;
import data.map.asset.FloorType;
import data.map.MapConfig;
import data.map.asset.GhostType;
import data.map.asset.AssetType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import core.AssetsHandler;
import core.event.Events;
import core.event.GameEvent;
import core.map.MapGenerator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class World {

    private static final String FLOOR_LAYER_NAME = "Floor Layer";
    private static final String BUILDING_LAYER_NAME = "Building Layer";
    private static final String GHOST_LAYER_NAME = "Ghost Layer";

    private final TiledMap map;
    private final MapConfig mapConfig;
    private final AssetsHandler assets;
    private final ColoredTiledMapRenderer mapRenderer;

    // tile map layer variables
    private final TiledMapTileLayer floorLayer;
    private final TiledMapTileLayer buildingLayer;
    private final TiledMapTileLayer ghostLayer;

    // grid for tile map layers
    private final FloorType[][] floorGrid;
    private final BuildingType[][] buildingGrid;
    private final GhostType<AssetType>[][] ghostGrid;

    private final int tilesWidth;
    private final int tilesHeight;

    private final float tileSize;
    private final float worldWidth;
    private final float worldHeight;


    private final long seed;


    public World(
        TiledMap map, MapConfig mapConfig, AssetsHandler assets,
        //pass layers in
        TiledMapTileLayer floorLayer,
        TiledMapTileLayer buildingLayer,
        TiledMapTileLayer ghostLayer,
        //pass grids in
        FloorType[][] floorGrid,
        BuildingType[][] buildingGrid,
        GhostType<AssetType>[][] ghostGrid
    ) {

        this.map = map;
        this.mapConfig = mapConfig;
        this.assets = assets;
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
        this.mapRenderer = new ColoredTiledMapRenderer(map,  unitScale);

        registPlacement();
    }

    public static class ColoredCell extends TiledMapTileLayer.Cell {
        private Color color = new Color(Color.WHITE);

        public ColoredCell(Color color) {
            this.color.set(color);
        }

        public Color getColor() {
            return color;
        }
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
        BuildingType[][] buildingGrid = new BuildingType[mapConfig.width][mapConfig.height];
        GhostType<AssetType>[][] ghostGrid = new GhostType[mapConfig.width][mapConfig.height];

        MapGenerator.applyToLayer(floorLayer, floorGrid, assets);


        GameEvent.MapGenerated.fire(mapConfig, floorGrid);

        return new World(map, mapConfig, assets,
            floorLayer, buildingLayer,
            ghostLayer, floorGrid, buildingGrid,
            ghostGrid);
    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void dispose() {
        mapRenderer.dispose();
        map.dispose();
    }

    public BuildingType getBuildingAt(int tileX, int tileY) {
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

    public GhostType<AssetType> getGhostTileAt(int tileX, int tileY) {
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

    public boolean isGhostTile(int tileX, int tileY) {
        if (!isInBounds(tileX, tileY)) return false;
        return ghostLayer.getCell(tileX, tileY) != null;
    }
    public boolean isGhostTile(float tileX, float tileY) {
        return isGhostTile((int)tileX, (int)tileY);
    }
    public boolean isGhostTile(Vector2 tile) {
        return isGhostTile((int)tile.x, (int)tile.y);
    }
    public boolean isGhostAtWorld(Vector2 worldPosition) {
        int tileX = MathUtils.floor(
            worldPosition.x / getTileSize()
        );

        int tileY = MathUtils.floor(
            worldPosition.y / getTileSize()
        );

        return isGhostTile(tileX, tileY);
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

    public AssetType getFloorName(int tileX, int tileY) {
        if (!isInBounds(tileX, tileY)) return null;
        //TODO: get floor name at x, y.
        //TODO: fix generic types.
        return floorGrid[tileX][tileY];
    }

    public boolean isWalkable(int tileX, int tileY) {
        FloorType floor = getFloorAt(tileX, tileY);
        return floor != null && floor != FloorType.WATER;
    }

    public boolean placeFloor(int tileX, int tileY, FloorType floorType) {
        return placeBlock(tileX, tileY, floorType);
    }

    public boolean placeBuilding(int tileX, int tileY, BuildingType buildingType) {
        return placeBlock(tileX, tileY, buildingType);
    }

    public boolean placeGhost(int tileX, int tileY, GhostType<? extends AssetType> ghostType) {
        return placeGhostBlock(tileX, tileY, ghostType);
    }

    public boolean placeBlock(
        int tileX, int tileY,
        AssetType type
    ) {
        if (type instanceof FloorType floorType) {
            //if (type == FloorType.WATER) return false;

            return placeInLayer(tileX, tileY, floorType, floorGrid, floorLayer);
        }

        if (type instanceof BuildingType buildingType) {

            return placeInLayer(tileX, tileY, buildingType, buildingGrid, buildingLayer);
        }

        return false;
    }

    private boolean placeGhostBlock(
        int tileX, int tileY,
        GhostType<? extends AssetType> ghostType
    ) {
        if (ghostType != null) {

            return placeInGhostLayer(tileX, tileY, ghostType, ghostGrid, ghostLayer);
        }

        return false;
    }

    private <T extends AssetType> boolean placeInLayer(
        int tileX, int tileY,
        T type, T[][] grid, TiledMapTileLayer layer
    ) {
        if (!isInBounds(tileX, tileY) || type == null) return false;

        if (grid[tileX][tileY] == type) return false;

        TiledMapTile tile = assets.getTile(type);
        if (tile == null) return false;

        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(tile);

        grid[tileX][tileY] = type;
        layer.setCell(tileX, tileY, cell);

        return true;
    }

    private boolean placeInGhostLayer(
        int tileX, int tileY,
        GhostType<? extends AssetType> ghostType,
        GhostType<? extends AssetType>[][] grid,
        TiledMapTileLayer ghostLayer
    ) {
        if (!isInBounds(tileX, tileY) || ghostType == null) return false;
        if (grid[tileX][tileY] == ghostType) return false;
        TiledMapTile tile = assets.getTile(ghostType.getSourceType());


        if (tile == null) return false;

        ColoredCell cell = new ColoredCell(ghostType.getState().getColor());
        cell.setTile(tile);
        grid[tileX][tileY] = ghostType;
        ghostLayer.setCell(tileX, tileY, cell);
        return true;
    }

    public void registPlacement() {
        Events.on(GameEvent.BlockPlaceRequest.class, request -> {

            if (request.isCancelled()) return;

            if (!isWalkable(request.tileX, request.tileY)) {
                request.cancel();
                return;
            }

            //floor tile type already there
            if (getFloorAt(request.tileX,  request.tileY) == request.type) {
                request.cancel();
                return;
            }

            if (placeBlock(request.tileX, request.tileY, request.type)) {
                GameEvent.BlockPlaced.fire(request.tileX, request.tileY, request.type);
            } else  {
                request.cancel();
            }
        });
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

    public MapConfig getMapConfig() {
        return mapConfig;
    }

}
