package core.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.InputHandler;
import core.Player;
import core.World;

public class BuildGhostLine {

    private final Player player;
    private final World world;
    private final InputHandler input;
    private final ShapeRenderer shapeRenderer;
    private final Viewport viewport;

    private final Vector3 mouseScreenStart = new Vector3();
    private final Vector3 mouseScreenTarget = new Vector3();

    private Vector3 worldStart = new Vector3();
    private Vector3 worldTarget = new Vector3();

    private final Vector3 tmp = new Vector3(); //temporary vector3 for calculations

    private float width;
    private Color color;

    private boolean isLineDrawing;

    public BuildGhostLine(World world, Player player, Viewport viewport, InputHandler input, ShapeRenderer shapeRenderer) {
        this.world = world;
        this.player = player;
        this.input = input;
        this.viewport = viewport;
        this.shapeRenderer = shapeRenderer;

        this.width = 1f;
        this.color = Color.WHITE;
        this.isLineDrawing = false;

    }

    public void update() {

        if (isLineDrawing) {
            mouseScreenTarget.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        }

        if (input.isMouseJustPressed(Input.Buttons.LEFT)) {
            if (!isLineDrawing) {
                mouseScreenStart.set(Gdx.input.getX(), Gdx.input.getY(), 0);

                //keep the start point in world at mouse drag started.
                worldStart.set(screenToTile(mouseScreenStart));
                worldStart = tileToMiddleTile(worldStart);
            }
        }

        if (input.isMousePressed(Input.Buttons.LEFT)) {
            if (!isLineDrawing) {
                isLineDrawing = true;
                mouseScreenTarget.set(Gdx.input.getX(),  Gdx.input.getY(), 0);
            }
        }

        if (input.isKeyPressed(Input.Keys.SHIFT_LEFT) && input.isMousePressed(Input.Buttons.LEFT)) {
            if (!isLineDrawing) {
                isLineDrawing = true;
                mouseScreenTarget.set(Gdx.input.getX(),  Gdx.input.getY(), 0);
            }
        }

        if (input.isMouseReleased(Input.Buttons.LEFT)) {
            isLineDrawing = false;
            mouseScreenStart.set(0, 0, 0);
            mouseScreenTarget.set(0, 0, 0);
            worldStart.set(0, 0, 0);
            worldTarget.set(0, 0, 0);
        }

        if (input.isKeyPressed(Input.Keys.F)) {
            System.out.println("isDiagonalLine: " + isDiagonalLine());
        }

    }

    public void draw() {
        if (!isLineDrawing) return;

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (isLineDrawing) drawLine();

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

    private void drawLine() {
        shapeRenderer.setColor(new Color(color));

        worldTarget.set(screenToTile(mouseScreenTarget));
        worldTarget = tileToMiddleTile(worldTarget);


        shapeRenderer.line(worldStart.x, worldStart.y, worldTarget.x, worldTarget.y);
    }

    private boolean isDiagonalLine() {
        double Angle45 = Math.PI / 4;
        return worldStart.x != worldTarget.x && worldStart.y != worldTarget.y;
    }

    private Vector3 screenToTile(Vector3 temp) {
        tmp.set(temp);
        viewport.unproject(tmp);

        return new Vector3 (MathUtils.floor(tmp.x / world.getTileSize()), MathUtils.floor(tmp.y / world.getTileSize()), 0);
    }

    private Vector3 tileToWorld(Vector3 temp) {
        return new Vector3(temp.x * world.getTileSize(), temp.y * world.getTileSize(), 0);
    }

    private Vector3 tileToMiddleTile(Vector3 temp) {
        return new Vector3(tileToWorld(temp).x + world.getTileSize() / 2, tileToWorld(temp).y + world.getTileSize() / 2, 0);
    }

}
