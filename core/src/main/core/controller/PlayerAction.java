package core.controller;

import Data.map.FloorType;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.BlockAssets;
import core.InputHandler;
import core.Player;
import core.World;
import core.entities.BuildGhostLine;
import core.entities.BuildPlan;
import core.event.Events;
import core.event.GameEvent;
import core.event.PlayerEvent;

import static com.badlogic.gdx.Input.Keys.*;

public class PlayerAction {
    private final World world;
    private final Player player;
    private final InputHandler input;
    private final ShapeRenderer shapeRenderer;
    private final BuildGhostLine buildGhostLine;
    private final Viewport viewport;

    private final Vector3 tmp = new Vector3();
    public FloorType selectedType;

    public enum PlaceMode {
        none, placing, breaking,
    }
    private PlaceMode placeMode = PlaceMode.none;
    private int selectX = -1, selectY = -1;
    public final Array<BuildPlan> linePlans = new Array<>();
    private final Array<BuildPlan> selecPlans = new Array<>();

    public PlayerAction(World world, Player player, Viewport viewport, InputHandler input, BlockAssets assets, ShapeRenderer shapeRenderer) {
        this.world = world;
        this.player = player;
        this.input = input;
        this.viewport = viewport;
        this.shapeRenderer = shapeRenderer;

        this.buildGhostLine = new BuildGhostLine(world, player, viewport, input, shapeRenderer);


        registPlacement(world, assets);
    }

    public void update() {

        buildGhostLine.update();

        if (input.isKeyJustPressed(NUM_1)) clearSelection();
        selectingBlock(input, NUM_2, FloorType.SAND);
        selectingBlock(input, NUM_3, FloorType.STONE);
        selectingBlock(input, NUM_4, FloorType.ROCK);

        if (selectedType == null) return; //block placing when selectedType = null

        if (input.isMousePressed(Input.Buttons.MIDDLE)) return; //panning = no place block

        int tx = screenToTileX(viewport, world);
        int ty = screenToTileY(viewport, world);

        if (!world.isInBounds(tx, ty)) {
            placeMode = PlaceMode.none;
            return;
        }


        //handle mouse press, start placement
        if (input.isMouseJustPressed(Input.Buttons.LEFT) && selectedType != null) {
            selectX = tx;
            selectY = ty;
            placeMode = PlaceMode.placing;
            updateLine(selectX, selectY, tx, ty);
        }

        //handle mouse drag, update line
        if (input.isMousePressed(Input.Buttons.LEFT) && placeMode == PlaceMode.placing) {
            updateLine(selectX, selectY, tx, ty);
        }

        //handle mouse release, end placement
        if (input.isMouseReleased(Input.Buttons.LEFT) && placeMode == PlaceMode.placing) {
            updateLine(selectX, selectY, tx, ty);
            flushPlans(linePlans);
            placeMode = PlaceMode.none;
            linePlans.clear();
        }

    }

    public void render() {
    }

    public void draw() {
        buildGhostLine.draw();
    }

    public void dispose() {
        buildGhostLine.dispose();
    }

    public void registPlacement(World world, BlockAssets assets) {
        Events.on(GameEvent.BlockPlaceRequest.class, request -> {

            if (request.isCancelled()) return;

            if (!world.isWalkable(request.tileX, request.tileY)) {
                request.cancel();
                return;
            }

            //floor tile type already there
            if (world.getFloorAt(request.tileX,  request.tileY) == request.floorType) {
                request.cancel();
                return;
            }

            if (world.placeFloor(request.tileX, request.tileY, request.floorType, assets)) {
                GameEvent.BlockPlaced.fire(request.tileX, request.tileY, request.floorType);
            } else  {
                request.cancel();
            }
        });
    }

    public FloorType getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(FloorType selectedType) {
        this.selectedType = selectedType;
    }

    private void updateLine(int startX, int startY, int endX, int endY) {
        linePlans.clear();

        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;
        int err = dx - dy;

        int x = startX;
        int y = startY;

        while (true) {
            linePlans.add(new BuildPlan(x, y, selectedType));

            if (x == endX && y == endY) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    private void flushPlans(Array<BuildPlan> plans) {
        for (BuildPlan plan : plans) {
            // add to player's build queue
            player.addBuildPlan(plan);
        }
    }


    private void selectingBlock(InputHandler input, int key, FloorType selectedType) {
        if (input.isKeyJustPressed(key)) {
            this.selectedType = selectedType;
            PlayerEvent.blockSelected.fire(selectedType);
        }
    }

    private void clearSelection() {
        selectedType = null;
        PlayerEvent.blockSelected.fire(null);
    }

    private int screenToTileX(Viewport viewport, World world) {
        tmp.set(com.badlogic.gdx.Gdx.input.getX(), com.badlogic.gdx.Gdx.input.getY(), 0f);
        viewport.unproject(tmp);
        return MathUtils.floor(tmp.x / world.getTileSize());
    }

    private int screenToTileY(Viewport viewport, World world) {
        //no need duplicate tmp.set(...);
        return MathUtils.floor(tmp.y / world.getTileSize());
    }

}
