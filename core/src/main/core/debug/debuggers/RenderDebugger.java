package core.debug.debuggers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import core.world.World;

public final class RenderDebugger {

    public RenderDebugger() {}

    public void update() {

    }

    public void drawTileBorders(World world, OrthographicCamera camera, ShapeRenderer shapeRenderer) {

        renderVisibleGrid(camera, shapeRenderer, world.getTileSize(), 0.05f, Color.DARK_GRAY);
    }

    public void drawChunkBorder(World world, OrthographicCamera camera, ShapeRenderer shapeRenderer) {

        renderVisibleGrid(camera, shapeRenderer, world.getChunkSize(), 0.08f, Color.WHITE);

    }

    private void renderVisibleGrid(
        OrthographicCamera camera,
        ShapeRenderer shapeRenderer,
        float cellSize,
        float thickness,
        Color color
    ) {
        float halfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        int minCellX = MathUtils.floor(
            (camera.position.x - halfWidth) / cellSize
        );
        int maxCellX = MathUtils.ceil(
            (camera.position.x + halfWidth) / cellSize
        );
        int minCellY = MathUtils.floor(
            (camera.position.y - halfHeight) / cellSize
        );
        int maxCellY = MathUtils.ceil(
            (camera.position.y + halfHeight) / cellSize
        );

        shapeRenderer.setColor(color);

        for (int x = minCellX; x <= maxCellX; x++) {
            for (int y = minCellY; y <= maxCellY; y++) {
                drawBorder(
                    shapeRenderer,
                    x * cellSize,
                    y * cellSize,
                    cellSize,
                    thickness
                );
            }
        }
    }

    private void drawBorder(
        ShapeRenderer shapeRenderer,
        float x,
        float y,
        float size,
        float thickness
    ) {
        float x2 = x + size;
        float y2 = y + size;

        shapeRenderer.rectLine(x, y, x2, y, thickness);
        shapeRenderer.rectLine(x2, y, x2, y2, thickness);
        shapeRenderer.rectLine(x2, y2, x, y2, thickness);
        shapeRenderer.rectLine(x, y2, x, y, thickness);
    }






}
