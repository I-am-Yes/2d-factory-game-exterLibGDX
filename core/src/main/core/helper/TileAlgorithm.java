package core.helper;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TileAlgorithm {

    public Array<Vector2> bresenhamTiles(int x0, int y0, int x1, int y1) {
        Array<Vector2> tiles = new Array<>();

        int dx = Math.abs(x1 - x0);
        int stepX = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0);
        int stepY = y0 < y1 ? 1 : -1;
        int error = dx + dy;

        while (true) {
            tiles.add(new Vector2(x0, y0));

            if (x0 == x1 && y0 == y1) {
                break;
            }

            int doubledError = 2 * error;

            if (doubledError >= dy) {
                error += dy;
                x0 += stepX;
            }

            if (doubledError <= dx) {
                error += dx;
                y0 += stepY;
            }
        }

        return tiles;
    }

    public Array<Vector2> bresenhamTiles(
        float startX, float startY,
        float endX, float endY,
        float size
    ) {
        int startTileX = MathUtils.floor(startX / size);
        int startTileY = MathUtils.floor(startY / size);
        int endTileX = MathUtils.floor(endX / size);
        int endTileY = MathUtils.floor(endY / size);

        return bresenhamTiles(
            startTileX, startTileY,
            endTileX, endTileY
        );
    }

    public Array<Vector2> DDATiles(float startX, float startY, float endX, float endY) {
        Array<Vector2> tiles = new Array<>();

        float deltaX = endX - startX;
        float deltaY = endY - startY;
        int steps = (int) Math.ceil(Math.max(Math.abs(deltaX), Math.abs(deltaY)));

        if (steps == 0) {
            tiles.add(new Vector2(
                (float) Math.floor(startX),
                (float) Math.floor(startY)
            ));
            return tiles;
        }

        float stepX = deltaX / steps;
        float stepY = deltaY / steps;
        int previousTileX = Integer.MIN_VALUE;
        int previousTileY = Integer.MIN_VALUE;

        float x = startX;
        float y = startY;

        for (int i = 0; i <= steps; i++) {
            int tileX = (int) Math.floor(x);
            int tileY = (int) Math.floor(y);

            if (tileX != previousTileX || tileY != previousTileY) {
                tiles.add(new Vector2(tileX, tileY));
                previousTileX = tileX;
                previousTileY = tileY;
            }

            x += stepX;
            y += stepY;
        }

        return tiles;
    }

    public Array<Vector2> thickBresenhamTiles(
        float startX, float startY,
        float endX, float endY,
        float width, float tileSize
    ) {
        Array<Vector2> result = new Array<>();

        float deltaX = endX - startX;
        float deltaY = endY - startY;
        float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (MathUtils.isZero(length)) {
            return bresenhamTiles(startX, startY, endX, endY, tileSize);
        }

        float normalX = -deltaY / length;
        float normalY = deltaX / length;

        int samples = Math.max(1, MathUtils.ceil(width / (tileSize * 0.5f)));
        float halfWidth = width * 0.5f;

        for (int i = 0; i <= samples; i++) {
            float offset = -halfWidth + width * i / samples;

            Array<Vector2> line = bresenhamTiles(
                startX + normalX * offset,
                startY + normalY * offset,
                endX + normalX * offset,
                endY + normalY * offset,
                tileSize
            );

            for (Vector2 tile : line) {
                if (!result.contains(tile, false)) {
                    result.add(tile);
                }
            }
        }

        return result;
    }

    public Array<Vector2> thickLineTiles(
        float startX, float startY,
        float endX, float endY,
        float width, float tileSize
    ) {
        Array<Vector2> tiles = new Array<>();

        float deltaX = endX - startX;
        float deltaY = endY - startY;
        float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (MathUtils.isZero(length)) {
            tiles.add(new Vector2(
                MathUtils.floor(startX / tileSize),
                MathUtils.floor(startY / tileSize)
            ));
            return tiles;
        }

        float halfWidth = width * 0.5f;
        float normalX = -deltaY / length * halfWidth;
        float normalY = deltaX / length * halfWidth;

        float[] rectangle = {
            startX + normalX, startY + normalY,
            startX - normalX, startY - normalY,
            endX - normalX, endY - normalY,
            endX + normalX, endY + normalY
        };

        float minX = Math.min(Math.min(rectangle[0], rectangle[2]),
            Math.min(rectangle[4], rectangle[6]));
        float maxX = Math.max(Math.max(rectangle[0], rectangle[2]),
            Math.max(rectangle[4], rectangle[6]));
        float minY = Math.min(Math.min(rectangle[1], rectangle[3]),
            Math.min(rectangle[5], rectangle[7]));
        float maxY = Math.max(Math.max(rectangle[1], rectangle[3]),
            Math.max(rectangle[5], rectangle[7]));

        for (int tileX = MathUtils.floor(minX / tileSize);
             tileX <= MathUtils.floor(maxX / tileSize);
             tileX++) {

            for (int tileY = MathUtils.floor(minY / tileSize);
                 tileY <= MathUtils.floor(maxY / tileSize);
                 tileY++) {

                if (rectangleIntersectsTile(rectangle, tileX, tileY, tileSize)) {
                    tiles.add(new Vector2(tileX, tileY));
                }
            }
        }

        return tiles;
    }

    private boolean rectangleIntersectsTile(
        float[] rectangle, int tileX, int tileY, float tileSize
    ) {
        float worldTileX = tileX * tileSize;
        float worldTileY = tileY * tileSize;

        if (!overlapsOnAxis(rectangle, worldTileX, worldTileY, tileSize, 1f, 0f)) return false;
        if (!overlapsOnAxis(rectangle, worldTileX, worldTileY, tileSize, 0f, 1f)) return false;

        for (int edge = 0; edge < 4; edge++) {
            int next = (edge + 1) % 4;

            float edgeX = rectangle[next * 2] - rectangle[edge * 2];
            float edgeY = rectangle[next * 2 + 1] - rectangle[edge * 2 + 1];

            if (!overlapsOnAxis(
                rectangle, worldTileX, worldTileY, tileSize, -edgeY, edgeX
            )) {
                return false;
            }
        }

        return true;
    }

    private boolean overlapsOnAxis(
        float[] rectangle, float tileX, float tileY, float tileSize,
        float axisX, float axisY
    ) {
        float rectangleMin = Float.MAX_VALUE;
        float rectangleMax = -Float.MAX_VALUE;

        for (int i = 0; i < 4; i++) {
            float projection = rectangle[i * 2] * axisX
                + rectangle[i * 2 + 1] * axisY;

            rectangleMin = Math.min(rectangleMin, projection);
            rectangleMax = Math.max(rectangleMax, projection);
        }

        float tileMin = Float.MAX_VALUE;
        float tileMax = -Float.MAX_VALUE;

        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                float projection = (tileX + x * tileSize) * axisX
                    + (tileY + y * tileSize) * axisY;

                tileMin = Math.min(tileMin, projection);
                tileMax = Math.max(tileMax, projection);
            }
        }

        return rectangleMax >= tileMin && tileMax >= rectangleMin;
    }

}
