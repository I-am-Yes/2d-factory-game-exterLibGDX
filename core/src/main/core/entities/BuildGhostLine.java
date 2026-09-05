package core.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.Player;
import core.World;

public class BuildGhostLine {

    private final Player player;
    private final World world;
    private final InputHandler input;
    private final Viewport viewport;
    private final ShapeRenderer shapeRenderer;

    private final Vector3 mouseScreenStart = new Vector3();
    private final Vector3 mouseScreenTarget = new Vector3();

    private Vector3 worldStart = new Vector3();
    private Vector3 worldTarget = new Vector3();

    private final Vector3 tmp = new Vector3(); //temporary vector3 for calculations

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

    private DrawLineMode drawLineMode = DrawLineMode.FREE_LINE;

    private boolean isFreeLineDrawing;
    private boolean isSnappedLineDrawing;
    private boolean isAngleSnappedLineDrawing;

    public BuildGhostLine(World world, Player player, Viewport viewport, InputHandler input, ShapeRenderer shapeRenderer) {
        this.world = world;
        this.player = player;
        this.input = input;
        this.viewport = viewport;
        this.shapeRenderer = shapeRenderer;

        this.width = 1f;
        this.color = Color.WHITE;
        this.isSnappedLineDrawing = false;

        this.tileSize = world.getTileSize();
    }

    public void update() {

        mouseScreenTarget.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        //if holding shift/ctrl AND left mouse is clicked, start the line
        if (input.isKeyPressed(Input.Keys.SHIFT_LEFT) && input.isKeyPressed(Input.Keys.CONTROL_LEFT)
            && input.isMouseJustPressed(Input.Buttons.LEFT)) {

            mouseScreenStart.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldStart.set(screenToWorld(mouseScreenStart));
            return;
        } else if ((input.isKeyPressed(Input.Keys.SHIFT_LEFT) || input.isKeyPressed(Input.Keys.CONTROL_LEFT))
            && input.isMouseJustPressed(Input.Buttons.LEFT)) {

            mouseScreenStart.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            //keep the start point in world at mouse drag started.
            worldStart.set(screenToTile(mouseScreenStart));
            worldStart = tileToMiddleTile(worldStart);
            return;
        }

        //if ctrl AND shift AND left mouse is holding, then free the line
        if (input.isKeyPressed(Input.Keys.CONTROL_LEFT)
            && input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            && input.isMousePressed(Input.Buttons.LEFT)) {
            if (!isFreeLineDrawing) {
                isFreeLineDrawing = true;
                isSnappedLineDrawing = false;
                isAngleSnappedLineDrawing = false;
            }

            mouseScreenTarget.set(Gdx.input.getX(),  Gdx.input.getY(), 0);
            return;
        }

        //if shift AND left mouse is holding, then angle snap the line
        if (input.isKeyPressed(Input.Keys.SHIFT_LEFT) && input.isMousePressed(Input.Buttons.LEFT)) {
            if (!isAngleSnappedLineDrawing) {
                isAngleSnappedLineDrawing = true;
                isSnappedLineDrawing = false;
                isFreeLineDrawing = false;
            }

            mouseScreenTarget.set(Gdx.input.getX(),  Gdx.input.getY(), 0);
            return;
        }

        //if ctrl AND left mouse is holding, then snap the line
        if (input.isKeyPressed(Input.Keys.CONTROL_LEFT) && input.isMousePressed(Input.Buttons.LEFT)) {
            if (!isSnappedLineDrawing) {
                isSnappedLineDrawing = true;
                isAngleSnappedLineDrawing = false;
                isFreeLineDrawing = false;
            }

            mouseScreenTarget.set(Gdx.input.getX(),  Gdx.input.getY(), 0);
            return;
        }

        //if left mouse is released, then clear the line
        if (input.isMouseReleased(Input.Buttons.LEFT)) {
            isFreeLineDrawing = false;
            isSnappedLineDrawing = false;
            isAngleSnappedLineDrawing = false;
            return;
        }

        if (input.isKeyPressed(Input.Keys.F)) {
            if (!isSnappedLineDrawing && !isAngleSnappedLineDrawing) {
                System.out.println("No line is being drawn");
            } else {
                System.out.println("isDiagonalLine: " + isDiagonalLine());
            }
        }

    }

    public void draw() {
        if (!isSnappedLineDrawing
            && !isAngleSnappedLineDrawing
                && !isFreeLineDrawing)
            return;

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (isSnappedLineDrawing) drawSnappedLine();
        else if (isAngleSnappedLineDrawing) drawAngleSnappedLine();
        else if (isFreeLineDrawing) drawFreeLine();

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }



    private void drawFreeLine() {
        shapeRenderer.setColor(new Color(color));

        worldTarget.set(screenToWorld(mouseScreenTarget));
        worldTarget = tileToWorld(worldTarget);

        shapeRenderer.line(worldStart.x, worldStart.y, worldTarget.x, worldTarget.y);
    }

    private void drawSnappedLine() {
        shapeRenderer.setColor(new Color(color));

        worldTarget.set(screenToTile(mouseScreenTarget));
        worldTarget = tileToMiddleTile(worldTarget);


        shapeRenderer.line(worldStart.x, worldStart.y, worldTarget.x, worldTarget.y);
    }

    private void drawAngleSnappedLine() {
        shapeRenderer.setColor(new Color(color));

        worldTarget.set(screenToTile(mouseScreenTarget));
        worldTarget = tileToMiddleTile(worldTarget);
        worldTarget = snapToAngleVector3(worldStart, worldTarget);

        shapeRenderer.line(worldStart.x, worldStart.y, worldTarget.x, worldTarget.y);
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
        return new Vector3(tempVec.x + tileSize / 2, tempVec.y + tileSize / 2, 0);
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
