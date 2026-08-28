package core;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class World {

    private static final String MAP_PATH =  "maps/world.tmx";
    private static final String FLOOR_LAYER_NAME = "Sand_tiles";
    private static final float TILE_PIXELS = 18f;

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final TiledMapTileLayer tileLayer;

    private final int tilesWidth;
    private final int tilesHeight;

    private final float tileSize;
    private final float worldWidth;
    private final float worldHeight;

    public World() {
        map = new TmxMapLoader().load(MAP_PATH);
        tileSize = 1f;
        mapRenderer = new OrthogonalTiledMapRenderer(map, tileSize / TILE_PIXELS);

        tileLayer = (TiledMapTileLayer) map.getLayers().get(FLOOR_LAYER_NAME);
        if (tileLayer == null) {
            throw new IllegalStateException("No \"tileLayer\" found: " + FLOOR_LAYER_NAME);
        }


        tilesWidth = tileLayer.getWidth();
        tilesHeight = tileLayer.getHeight();
        worldWidth = tilesWidth * tileSize;
        worldHeight = tilesHeight * tileSize;

    }

    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void dispose() {
        mapRenderer.dispose();
        map.dispose();
    }

    public boolean isInBounds(int tileX, int tileY) {
        return tileX >= 0 && tileX < tilesWidth && tileY >= 0 && tileY < tilesHeight;
    }

    public boolean hasTile(int tileX, int tileY) {
        if (!isInBounds(tileX, tileY)) return false;
        return tileLayer.getCell(tileX, tileY) != null;
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
    public float getTileWidth() {
        return tilesWidth;
    }
    public float getTileHeight() {
        return tilesHeight;
    }
    public float getWorldWidth() {
        return worldWidth;
    }
    public float getWorldHeight() {
        return worldHeight;
    }

}
