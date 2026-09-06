package core.entities;

import Data.map.FloorType;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.Player;
import core.World;
import core.event.GameEvent;
import core.helper.TileAlgorithm;

public class BuildGhostLine {

    private final Player player;
    private final World world;
    private final InputHandler input;
    private final Viewport viewport;
    private final ShapeRenderer shapeRenderer;

    private final TileAlgorithm tileAlgorithm = new TileAlgorithm();

    private final Vector3 mouseScreenStart = new Vector3();
    private final Vector3 mouseScreenTarget = new Vector3();

    private Vector3 worldStart = new Vector3();
    private Vector3 worldTarget = new Vector3();

    private final Vector3 tmp = new Vector3(); //temporary vector3 for calculations
    private Array<Vector2> tiledLine;
    private Array<FloorType> translatedTiledLine;

    private final float snapAngle = MathUtils.PI / 4f; //45, 90, ... degrees

    private float width;
    private Color color;

    private final float tileSize;

    private enum DrawLineMode {
        NONE,
        FREE_LINE,
        TILE_SNAP_LINE,
        ANGLE_SNAP_LINE
    }
    private DrawLineMode drawLineMode = DrawLineMode.NONE;

    private enum PlacingAlgorithm {
        NONE,
        DDA_LINE,
        BRESHENHAM_LINE,
        THICK_LINE
    }
    private PlacingAlgorithm placingAlgorithm = PlacingAlgorithm.THICK_LINE;

    public BuildGhostLine(World world, Player player, Viewport viewport, InputHandler input, ShapeRenderer shapeRenderer) {
        this.world = world;
        this.player = player;
        this.input = input;
        this.viewport = viewport;
        this.shapeRenderer = shapeRenderer;

        this.width = 0.01f;
        this.color = Color.WHITE;
        this.tileSize = world.getTileSize();
    }

    public void update() {

        if (drawLineMode != DrawLineMode.NONE)
            if (placingAlgorithm == PlacingAlgorithm.BRESHENHAM_LINE)
                tiledLine = bresenhamTileArray();
            else if (placingAlgorithm == PlacingAlgorithm.DDA_LINE)
                tiledLine = DDATileArray();
            else if (placingAlgorithm == PlacingAlgorithm.THICK_LINE)
                tiledLine = thickLineTileArray();

        if (input.isMouseReleased(Input.Buttons.LEFT)) {
            if (tiledLine != null) {
                for (int i = 0; i < tiledLine.size; i++) {
                    Vector2 tile = tiledLine.get(i);
                    GameEvent.BlockPlaceRequest.fire((int) tile.x, (int) tile.y, FloorType.DIRT);
                }

                tiledLine.clear();
            }
            drawLineMode = DrawLineMode.NONE;
            return;
        }

        if (!input.isMousePressed(Input.Buttons.LEFT)) {
            return;
        }

        if (input.isMousePressed(Input.Buttons.LEFT)
            && getCurrentLineMode() != DrawLineMode.NONE) {
            drawLineMode = getCurrentLineMode();
        }

        mouseScreenTarget.set(input.getMousePos(), 0);

        if (input.isMouseJustPressed(Input.Buttons.LEFT)
            && getCurrentLineMode() != DrawLineMode.NONE) {
            drawLineMode = getCurrentLineMode();

            mouseScreenStart.set(input.getMousePos(), 0);

            if (drawLineMode == DrawLineMode.FREE_LINE) {
                worldStart.set(screenToWorld(mouseScreenStart));
            } else {
                worldStart.set(screenToTile(mouseScreenStart));
                worldStart = tileToMiddleTile(worldStart);
            }
        }


        if (input.isKeyPressed(Input.Keys.F)) {
            if (getCurrentLineMode() == DrawLineMode.NONE) {
                System.out.println("No line is being drawn");
            } else {
                System.out.println("isDiagonalLine: " + isDiagonalLine());
            }
        }
        if (input.isMouseJustPressed(Input.Buttons.RIGHT)) {
            System.out.println("tiledLine: " + tiledLine);
            System.out.println("tiledLine contents: " + translatedTiledLine);
        }

    }

    public void draw() {
        if (drawLineMode == DrawLineMode.NONE) return;

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (drawLineMode == DrawLineMode.FREE_LINE)
            drawFreeLine();
        else if (drawLineMode == DrawLineMode.ANGLE_SNAP_LINE)
            drawAngleSnappedLine();
        else if (drawLineMode == DrawLineMode.TILE_SNAP_LINE)
            drawTileSnappedLine();

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }



    public Array<Vector2> getCurrentTiledLine() {
        return tiledLine;
    }

    public Array<FloorType> getCurrentTranslatedTiledLine() {
        return translateTiledLineToFloorType(tiledLine);
    }

    public DrawLineMode getCurrentPlayerLineMode() {
        return drawLineMode;
    }

    private Array<Vector2> DDATileArray() {
        return tileAlgorithm.DDATiles(
            worldStart.x,
            worldStart.y,
            worldTarget.x,
            worldTarget.y
        );
    }

    private Array<Vector2> bresenhamTileArray() {
        return new Array<>(
            tileAlgorithm.bresenhamTiles(worldStart.x, worldStart.y,
                worldTarget.x, worldTarget.y, tileSize));

    }

    private Array<Vector2> thickLineTileArray() {
        return tileAlgorithm.thickLineTiles(
            worldStart.x, worldStart.y,
            worldTarget.x, worldTarget.y,
            width, tileSize
        );
    }

    private Array<FloorType> translateTiledLineToFloorType(Array<Vector2> tiledLine) {
        if (tiledLine.isEmpty()) return null;
        Array<FloorType> tileArray = new Array<>();
        for (int i = 0; i < tiledLine.size; i++) {
            tileArray.add(world.getFloorAt(tiledLine.get(i)));
        }
        translatedTiledLine = tileArray;
        return translatedTiledLine;
    }

    private void drawFreeLine() {
        shapeRenderer.setColor(new Color(color));

        worldTarget.set(screenToWorld(mouseScreenTarget));
        worldTarget = tileToWorld(worldTarget);

        shapeRenderer.rectLine(worldStart.x, worldStart.y, worldTarget.x, worldTarget.y, width);
    }

    private void drawTileSnappedLine() {
        shapeRenderer.setColor(new Color(color));

        worldTarget.set(screenToTile(mouseScreenTarget));
        worldTarget = tileToMiddleTile(worldTarget);


        shapeRenderer.rectLine(worldStart.x, worldStart.y, worldTarget.x, worldTarget.y, width);
    }

    private void drawAngleSnappedLine() {
        shapeRenderer.setColor(new Color(color));

        worldTarget.set(screenToTile(mouseScreenTarget));
        worldTarget = tileToMiddleTile(worldTarget);
        worldTarget = snapToAngleVector3(worldStart, worldTarget);

        shapeRenderer.rectLine(worldStart.x, worldStart.y, worldTarget.x, worldTarget.y, width);
    }

    private DrawLineMode getCurrentLineMode() {
        boolean shift = input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        boolean ctrl = input.isKeyPressed(Input.Keys.CONTROL_LEFT);

        if (ctrl && shift) return DrawLineMode.FREE_LINE;
        if (ctrl) return DrawLineMode.TILE_SNAP_LINE;
        if (shift) return DrawLineMode.ANGLE_SNAP_LINE;

        return DrawLineMode.NONE;
    }

    private boolean isDiagonalLine() {
        return worldStart.x != worldTarget.x && worldStart.y != worldTarget.y;
    }

    private Vector3 screenToTile(Vector3 temp) {
        tmp.set(temp);
        viewport.unproject(tmp);
        return new Vector3 (MathUtils.floor(tmp.x / tileSize), MathUtils.floor(tmp.y / tileSize), 0);
    }

    private Vector3 screenToWorld(Vector3 temp) {
        tmp.set(temp);
        viewport.unproject(tmp);
        return new Vector3(tmp.x, tmp.y, 0);
    }

    private Vector3 tileToWorld(Vector3 temp) {
        return new Vector3(temp.x, temp.y, 0);
    }

    private Vector3 tileToWorldTile(Vector3 temp) {
        return new Vector3(temp.x * tileSize, temp.y * tileSize, 0);
    }

    private Vector3 tileToMiddleTile(Vector3 temp) {
        Vector3 tempVec = tileToWorldTile(temp);
        return new Vector3(tempVec.x + tileSize / 2.1f, tempVec.y + tileSize / 2.1f, 0);
    }

    private Vector3 snapToAngleVector3(Vector3 start, Vector3 target) {
        float deltaX = target.x - start.x;
        float deltaY = target.y - start.y;

        float angle = MathUtils.atan2(deltaY, deltaX);
        float snappedAngle = MathUtils.round(angle / snapAngle) * snapAngle;

        float directionX = MathUtils.cos(snappedAngle);
        float directionY = MathUtils.sin(snappedAngle);

        float snappedLength = deltaX * directionX + deltaY * directionY;

        return new Vector3(
            start.x + directionX * snappedLength,
            start.y + directionY * snappedLength,
            target.z
        );
    }

}
