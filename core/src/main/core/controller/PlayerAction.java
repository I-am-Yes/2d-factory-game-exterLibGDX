package core.controller;

import Data.map.FloorType;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.BlockAssets;
import core.InputHandler;
import core.World;
import core.event.Events;
import core.event.GameEvent;
import core.event.PlayerEvent;

import static com.badlogic.gdx.Input.Keys.*;

public class PlayerAction {

    private final Vector3 tmp = new Vector3();
    public FloorType selectedType;

    public void update(InputHandler input, World world, Viewport viewport) {

        if (input.isKeyJustPressed(NUM_1)) clearSelection();
        selectingBlock(input, NUM_2, FloorType.SAND);
        selectingBlock(input, NUM_3, FloorType.STONE);
        selectingBlock(input, NUM_4, FloorType.ROCK);

        if (selectedType == null) return; //block placing when selectedType = null

        if (!input.isMousePressed(Input.Buttons.LEFT)) return;
        if (input.isMousePressed(Input.Buttons.MIDDLE)) return; //panning = no place block

        int tx = screenToTileX(viewport, world);
        int ty = screenToTileY(viewport, world);
        if (!world.isInBounds(tx, ty)) return;

        GameEvent.BlockPlaceRequest.fire(tx, ty, selectedType);

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
