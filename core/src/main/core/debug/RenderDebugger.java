package core.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import core.world.World;

public final class RenderDebugger {

    public RenderDebugger() {}

    public void drawTileBorders(World world, OrthographicCamera camera, ShapeRenderer shapeRenderer) {

        float tileSize = world.getTileSize();
        int tilesWidth = (int) world.getTilesWidth();
        int tilesHeight = (int) world.getTilesHeight();

        //render only on visible on camera view
        float halfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        int minX = MathUtils.clamp((int) Math.floor(camera.position.x - halfWidth), 0, tilesWidth - 1);
        int maxX = MathUtils.clamp((int) Math.ceil(camera.position.x + halfWidth), 0, tilesWidth - 1);
        int minY = MathUtils.clamp((int) Math.floor(camera.position.y - halfHeight), 0, tilesHeight - 1);
        int maxY = MathUtils.clamp((int) Math.ceil(camera.position.y + halfHeight), 0, tilesHeight - 1);

        shapeRenderer.setColor(Color.DARK_GRAY);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                float wx = x * tileSize;
                float wy = y * tileSize;
                shapeRenderer.rect(wx, wy, tileSize, tileSize);
            }
        }
    }






}
