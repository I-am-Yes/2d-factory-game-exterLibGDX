package core.player;

import core.machine.state.Direction;
import core.system.systems.PlayerSystem;
import data.map.asset.AssetType;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.world.World;
import core.player.mechanic.BuildGhostLine;
import core.event.events.GameEvent;
import ui.PlayerHotbar;

import static com.badlogic.gdx.Input.Keys.*;
import static core.app.vars.Cores.*;
import static core.app.vars.Vars.*;

public class PlayerAction {
    private final BuildGhostLine buildGhostLine;

    private final Vector3 tmp = new Vector3();
    private Direction placementDirection = Direction.EAST; //default startup direction

    private AssetType selectedType;

    public enum PlaceMode {
        none, ghost, placing, breaking,
    }
    private PlaceMode placeMode = PlaceMode.none;
    private int selectX = -1, selectY = -1;


    public PlayerAction() {
        this.buildGhostLine = new BuildGhostLine(world, player, viewport, this, input, assets);
    }

    public void update() {

        if (uiInputGate.isPointerOverGUI()) {
            placeMode = PlaceMode.none;
            return;
        }

        updateRotateBlock();
        buildGhostLine.update(getSelectedType());
        updateRemovingBlock();
        updatePlacingBlock();
    }

    public void drawShapeRenderer() {
        buildGhostLine.drawShapeRenderer();
    }

    public void dispose() {
        buildGhostLine.dispose();
    }

    private void updateRemovingBlock() {
        if (placeMode == PlaceMode.placing || selectedType != null) {
            if (placeMode == PlaceMode.breaking) {
                placeMode = PlaceMode.none;
            }
            return;
        }

        if (!input.isMousePressed(Input.Buttons.RIGHT)) {
            if (placeMode == PlaceMode.breaking) {
                placeMode = PlaceMode.none;
            }
            return;
        }

        int tx = PlayerSystem.getHoverTileThresholdX();
        int ty = PlayerSystem.getHoverTileThresholdY();

        if (!world.isInBounds(tx, ty)) {
            placeMode = PlaceMode.none;
            return;
        }

        boolean justPressed =
            input.isMouseJustPressed(Input.Buttons.RIGHT);

        if (justPressed) {
            placeMode = PlaceMode.breaking;
        }

        if (placeMode != PlaceMode.breaking) return;

        // Remove once on the initial click or after entering another tile.
        if (justPressed || tx != selectX || ty != selectY) {
            selectX = tx;
            selectY = ty;

            GameEvent.BlockRemoveRequest.fire(tx, ty);
        }
    }

    private void updatePlacingBlock() {
        if (input.isMouseReleased(Input.Buttons.LEFT)) {
            if (placeMode == PlaceMode.placing) {
                placeMode = PlaceMode.none;
            }
            return;
        }

        if (placeMode == PlaceMode.ghost
            || selectedType == null
            || input.isMousePressed(Input.Buttons.MIDDLE)
            || !input.isMousePressed(Input.Buttons.LEFT)) {
            return;
        }

        int tx = PlayerSystem.getHoverTileThresholdX();
        int ty = PlayerSystem.getHoverTileThresholdY();

        if (!world.isInBounds(tx, ty)) {
            placeMode = PlaceMode.none;
            return;
        }

        if (input.isMouseJustPressed(Input.Buttons.LEFT)) {
            placeMode = PlaceMode.placing;
        }

        if (placeMode != PlaceMode.placing) {
            return;
        }

        selectX = tx;
        selectY = ty;
        GameEvent.BlockPlaceRequest.fire(tx, ty, placementDirection, selectedType);
    }

    private void updateRotateBlock() {
        this.selectedType = PlayerHotbar.getSelectedType();
        if (selectedType == null) placementDirection = Direction.EAST;

        if (input.isKeyJustPressed(R)) {
            boolean shift = input.isKeyPressed(SHIFT_LEFT);
            boolean control = input.isKeyPressed(CONTROL_LEFT);

            if (shift && control) {
                placementDirection = placementDirection.rotateHalfCounterClockwise();
            } else if (shift) {
                placementDirection = placementDirection.rotateCounterClockwise();
            } else if (control) {
                placementDirection = placementDirection.rotateHalfClockwise();
            } else {
                placementDirection = placementDirection.rotateClockwise();
            }

            return;
        }
    }

    public PlaceMode getPlacingMode() {
        return placeMode;
    }
    public void setPlaceMode(PlaceMode placeMode) {
        this.placeMode = placeMode;
    }
    public AssetType getSelectedType() {
        return selectedType;
    }
    public BuildGhostLine getBuildGhostLine() {
        return buildGhostLine;
    }

    public Direction getPlacementDirection() {
        return placementDirection;
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
