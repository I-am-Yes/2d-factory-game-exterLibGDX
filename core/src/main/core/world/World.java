package core.world;

import core.controller.camera.CameraController;
import core.controller.camera.CameraViewMode;
import core.world.chunk.Chunk;
import core.world.chunk.ChunkManager;
import core.world.chunk.ChunkRenderDetail;
import core.world.chunk.ChunkRenderer;
import data.map.asset.BuildingType;
import data.map.asset.FloorType;
import data.map.MapConfig;
import data.map.asset.GhostType;
import data.map.asset.AssetType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import core.assets.AssetsHandler;
import core.event.Events;
import core.event.GameEvent;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.function.Supplier;

public class World {

    private static final String FLOOR_LAYER_NAME = "Floor Layer";
    private static final String BUILDING_LAYER_NAME = "Building Layer";
    private static final String GHOST_LAYER_NAME = "Ghost Layer";

    private final TiledMap map;
    private final MapConfig mapConfig;
    private final AssetsHandler assets;
    private final ColoredTiledMapRenderer mapRenderer;
    private final ChunkRenderer chunkRenderer;
    private final ChunkManager chunkManager;

    private static final int MAX_CHUNK_LOAD_RADIUS = 16;
    private static final int UNLOAD_CHUNK_PADDING = 2;
    private static final int CHUNK_LOAD_BUDGET = 1; //max chunks load per frame

    private int lastStreamChunkX = Integer.MIN_VALUE;
    private int lastStreamChunkY = Integer.MIN_VALUE;
    private int lastStreamRadius = Integer.MIN_VALUE;
    private boolean chunkStreamComplete;

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

    private World(MapConfig mapConfig, AssetsHandler assets) {
        this.map = null;
        this.mapConfig = mapConfig;
        this.assets = assets;
        this.seed = mapConfig.seed;

        this.floorLayer = null;
        this.buildingLayer = null;
        this.ghostLayer = null;

        this.floorGrid = null;
        this.buildingGrid = null;
        this.ghostGrid = null;

        // Temporary viewport defaults; not world boundaries.
        this.tilesWidth = 48;
        this.tilesHeight = 27;
        this.tileSize = 1f;
        this.worldWidth = 512f;
        this.worldHeight = 512f;

        this.mapRenderer = null;
        this.chunkRenderer = new ChunkRenderer(assets);
        this.chunkManager = new ChunkManager(mapConfig);

        registPlacement();
    }

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
        this.chunkRenderer = new ChunkRenderer(assets);
        this.chunkManager = new ChunkManager(mapConfig);

        registPlacement();
    }

    public static class ColoredCell extends TiledMapTileLayer.Cell {
        private final Color color = new Color(Color.WHITE);

        public ColoredCell(Color color) {
            this.color.set(color);
        }

        public Color getColor() {
            return color;
        }
    }

    public static World generateWorld(
        MapConfig mapConfig,
        AssetsHandler assets
    ) {
        return new World(mapConfig, assets);
    }

//    public static World generateWorld(MapConfig mapConfig, AssetsHandler assets) {
//        TiledMap map = new TiledMap();
//
//        map.getProperties().put("width", mapConfig.width);
//        map.getProperties().put("height", mapConfig.height);
//        map.getProperties().put("tilewidth", assets.getTileWidth());
//        map.getProperties().put("tileheight", assets.getTileHeight());
//
//        map.getTileSets().addTileSet(assets.buildTileSet());
//
//        //create layers
//        TiledMapTileLayer floorLayer = new TiledMapTileLayer(
//            mapConfig.width, mapConfig.height,
//            assets.getTileWidth(), assets.getTileHeight()
//        );
//        TiledMapTileLayer buildingLayer = new TiledMapTileLayer(
//            mapConfig.width, mapConfig.height,
//            assets.getTileWidth(), assets.getTileHeight()
//        );
//        TiledMapTileLayer ghostLayer = new TiledMapTileLayer(
//            mapConfig.width, mapConfig.height,
//            assets.getTileWidth(), assets.getTileHeight()
//        );
//
//        //set layer name
//        floorLayer.setName(FLOOR_LAYER_NAME);
//        buildingLayer.setName(BUILDING_LAYER_NAME);
//        ghostLayer.setName(GHOST_LAYER_NAME);
//
//        //add layer to map
//        map.getLayers().add(floorLayer);
//        map.getLayers().add(buildingLayer);
//        map.getLayers().add(ghostLayer);
//
//        //generate layer grid
//        FloorType[][] floorGrid = MapGenerator.generateTiledMap(mapConfig);
//        BuildingType[][] buildingGrid = new BuildingType[mapConfig.width][mapConfig.height];
//        GhostType<AssetType>[][] ghostGrid = new GhostType[mapConfig.width][mapConfig.height];
//
//        MapGenerator.applyToLayer(floorLayer, floorGrid, assets);
//
//
//        GameEvent.MapGenerated.fire(mapConfig, floorGrid);
//
//        return new World(map, mapConfig, assets,
//            floorLayer, buildingLayer,
//            ghostLayer, floorGrid, buildingGrid,
//            ghostGrid);
//    }

    public void update(OrthographicCamera camera) {
        int cameraTileX = MathUtils.floor(camera.position.x / getTileSize());
        int cameraTileY = MathUtils.floor(camera.position.y / getTileSize());

        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;
        int requestedRadius = Math.max(
            1,
            MathUtils.ceil(
                Math.max(visibleWidth, visibleHeight)
                    / getTileSize()
                    / Chunk.SIZE
                    / 2f
            )
        );

        int loadRadius = Math.min(requestedRadius, MAX_CHUNK_LOAD_RADIUS);
        int centerChunkX = Math.floorDiv(cameraTileX, Chunk.SIZE);
        int centerChunkY = Math.floorDiv(cameraTileY, Chunk.SIZE);

        boolean streamTargetChanged =
            centerChunkX != lastStreamChunkX
                || centerChunkY != lastStreamChunkY
                || loadRadius != lastStreamRadius;

        if (streamTargetChanged) {
            lastStreamChunkX = centerChunkX;
            lastStreamChunkY = centerChunkY;
            lastStreamRadius = loadRadius;
            chunkStreamComplete = false;

            chunkManager.unloadOutsideChunk(
                centerChunkX,
                centerChunkY,
                loadRadius + UNLOAD_CHUNK_PADDING,
                chunkRenderer::unload
            );
        }

        if (chunkStreamComplete) {
            return;
        }

        chunkStreamComplete =
            chunkManager.loadChunksAroundTile(
                cameraTileX,
                cameraTileY,
                loadRadius,
                CHUNK_LOAD_BUDGET
            );

        chunkRenderer.invalidateVisibleChunks();

//        mapRenderer.setView(camera);
//        mapRenderer.render();
    }

    public void drawCached(OrthographicCamera camera, Supplier<CameraController> cameraControllerSupplier) {
        CameraViewMode viewMode = cameraControllerSupplier.get().getCurrentViewMode();
        ChunkRenderDetail renderDetail = cameraControllerSupplier.get().getMapRenderDetail();
        chunkRenderer.drawCached(
            camera, chunkManager, viewMode, renderDetail, getTileSize()
        );
    }

    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
        if (chunkRenderer != null) chunkRenderer.dispose();
    }

    private Chunk getChunkAt(int tileX, int tileY) {
        return chunkManager.getOrCreateChunkForTile(tileX, tileY);
    }

    public BuildingType getBuildingAt(int tileX, int tileY) {
        Chunk chunk = getChunkAt(tileX, tileY);

        int localX = chunkManager.getLocalX(tileX);
        int localY = chunkManager.getLocalY(tileY);

        return chunk.buildings[localX][localY];
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
        Chunk chunk = getChunkAt(tileX, tileY);

        int localX = chunkManager.getLocalX(tileX);
        int localY = chunkManager.getLocalY(tileY);

        return chunk.ghosts[localX][localY];
    }

    public boolean isInBounds(int tileX, int tileY) {
        return true;
    }

    public boolean hasTile(int tileX, int tileY) {
        if (!isInBounds(tileX, tileY)) return false;
        if (getFloorAt(tileX, tileY) != null) return true;
        if (getBuildingAt(tileX, tileY) != null) return true;
        return getGhostTileAt(tileX, tileY) != null;
    }

    public boolean isGhostTile(int tileX, int tileY) {
        return getGhostTileAt(tileX, tileY) != null;
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
        Chunk chunk = getChunkAt(tileX, tileY);

        int localX = chunkManager.getLocalX(tileX);
        int localY = chunkManager.getLocalY(tileY);

        return chunk.floors[localX][localY];
    }

    public FloorType getFloorAt(float tileX, float tileY) {
        return getFloorAt((int)tileX, (int)tileY);
    }

    public FloorType getFloorAt(Vector2 tile) {
        return getFloorAt((int)tile.x, (int)tile.y);
    }

    public String getTileAssetName(Vector2 tile) {
        return getTileAssetName((int)tile.x, (int)tile.y);
    }

    public String getTileAssetName(int tileX, int tileY) {
        GhostType<AssetType> ghost = getGhostTileAt(tileX, tileY);

        if (ghost != null) {
            return "Ghost " + ghost.getSourceType().getNamePNG();
        }

        BuildingType building = getBuildingAt(tileX, tileY);
        if (building != null) {
            return building.getNamePNG();
        }

        FloorType floor = getFloorAt(tileX, tileY);
        if (floor != null) {
            return floor.getNamePNG();
        }

        return "None";
    }

    public String getTileName(Vector2 tile) {
        return getTileName((int)tile.x, (int)tile.y);
    }

    public String getTileName(int tileX, int tileY) {
        GhostType<AssetType> ghost = getGhostTileAt(tileX, tileY);

        if (ghost != null) {
            return "Ghost " + ghost.getSourceType().getName();
        }

        BuildingType building = getBuildingAt(tileX, tileY);
        if (building != null) {
            return building.getName();
        }

        FloorType floor = getFloorAt(tileX, tileY);
        if (floor != null) {
            return floor.getName();
        }

        return "None";
    }

    public boolean isWalkable(int tileX, int tileY) {
        FloorType floor = getFloorAt(tileX, tileY);
        return floor != null && floor != FloorType.WATER;
    }

    public boolean placeFloor(int tileX, int tileY, FloorType floorType) {
        if (floorType == null) return false;

        Chunk chunk = getChunkAt(tileX, tileY);
        int localX = chunkManager.getLocalX(tileX);
        int localY = chunkManager.getLocalY(tileY);

        if (chunk.floors[localX][localY] == floorType) return false;

        chunk.floors[localX][localY] = floorType;
        chunk.dirty = true;
        chunk.markRenderDirty();
        chunkRenderer.invalidateOverview();
        return true;
    }

    public boolean placeBuilding(
        int tileX, int tileY, BuildingType buildingType
    ) {
        if (buildingType == null || !canPlaceBuildingAt(tileX, tileY)) {
            return false;
        }

        Chunk chunk = getChunkAt(tileX, tileY);
        int localX = chunkManager.getLocalX(tileX);
        int localY = chunkManager.getLocalY(tileY);

        chunk.buildings[localX][localY] = buildingType;
        chunk.dirty = true;
        chunk.markRenderDirty();
        return true;
    }

    public boolean placeGhost(
        int tileX, int tileY, GhostType<? extends AssetType> ghostType
    ) {
        if (ghostType == null) return false;

        Chunk chunk = getChunkAt(tileX, tileY);
        int localX = chunkManager.getLocalX(tileX);
        int localY = chunkManager.getLocalY(tileY);

        @SuppressWarnings("unchecked")
        GhostType<AssetType> storedGhost =
            (GhostType<AssetType>) ghostType;

        chunk.ghosts[localX][localY] = storedGhost;
        chunk.dirty = true;
        chunk.markRenderDirty();
        return true;
    }

    public boolean placeBlock(int tileX, int tileY, AssetType type) {
        if (type instanceof FloorType floorType) {
            return placeFloor(tileX, tileY, floorType);
        }

        if (type instanceof BuildingType buildingType) {
            return placeBuilding(tileX, tileY, buildingType);
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

            //TODO: get a dynamic multi layer checker system.
            //floor tile type already there
            if (getFloorAt(request.tileX,  request.tileY) == request.type) {
                request.cancel();
                return;
            }

            if (getBuildingAt(request.tileX,  request.tileY) == request.type) {
                request.cancel();
                return;
            }

            if (placeBlock(request.tileX, request.tileY, request.type)) {
                GameEvent.BlockPlaced.fire(request.tileX, request.tileY, request.type, request.direction);
            } else  {
                request.cancel();
            }
        });
    }

    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    public int worldToTileX(int worldX) {
        return (int) (worldX / tileSize);
    }
    public int worldToTileY(int worldY) {
        return (int) (worldY / tileSize);
    }

    public long getSeed() {
        return seed;
    }

    public int getChunkSize() {
        return Chunk.SIZE;
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

    public FloorType[][] getFloorGrid() {
        return floorGrid;
    }
    public BuildingType[][] getBuildingGrid() {
        return buildingGrid;
    }
    public GhostType<? extends AssetType>[][] getGhostGrid() {
        return ghostGrid;
    }

    public ChunkRenderDetail getCurrentRenderDetail() {
        return chunkRenderer.getCurrentDetail();
    }

}
