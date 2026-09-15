package core.player;

import core.UiInputGate;
import core.system.systems.PlayerSystem;
import data.map.asset.AssetType;
import data.map.asset.BuildingType;
import data.map.asset.FloorType;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.AssetsHandler;
import core.InputHandler;
import core.world.World;
import core.player.mechanic.BuildGhostLine;
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
    private final SpriteBatch spriteBatch;
    private final AssetsHandler assets;
    private final UiInputGate uiInputGate;

    private final Vector3 tmp = new Vector3();

    private AssetType selectedType;

    public enum PlaceMode {
        none, ghost, placing, breaking,
    }
    private PlaceMode placeMode = PlaceMode.none;
    private int selectX = -1, selectY = -1;

    private float delta;

    public PlayerAction(World world, Viewport viewport, Player player, InputHandler input, UiInputGate uiInputGate, SpriteBatch playerSpriteBatch, ShapeRenderer shapeRenderer, AssetsHandler assets) {
        this.world = world;
        this.player = player;
        this.input = input;
        this.viewport = viewport;
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = playerSpriteBatch;
        this.assets = assets;

        this.uiInputGate = uiInputGate;

        this.buildGhostLine = new BuildGhostLine(world, player, viewport, this, input, shapeRenderer, assets);
    }

    public void update(float delta) {
        this.delta = delta;

        buildGhostLine.update(getSelectedType());

        if (input.isKeyJustPressed(NUM_1)) clearSelection();
        selectingBlock(input, NUM_2, FloorType.SAND);
        selectingBlock(input, NUM_3, FloorType.STONE);
        selectingBlock(input, NUM_4, BuildingType.HAZARD_BLOCK);
        selectingBlock(input, NUM_5, BuildingType.HAZARD_BLOCK2);


        updatePlacingBlock();
    }

    public void render() {}

    public void draw() {
        buildGhostLine.draw();
    }

    public void dispose() {
        buildGhostLine.dispose();
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

    public void setSelectedType(AssetType selectedType) {
        this.selectedType = selectedType;
    }

    public BuildGhostLine getBuildGhostLine() {
        return buildGhostLine;
    }

    private void updatePlacingBlock() {
        if (input.isMouseReleased(Input.Buttons.LEFT)) {
            if (placeMode == PlaceMode.placing) {
                placeMode = PlaceMode.none;
            }
            return;
        }

        if (uiInputGate.isPointerOverGUI()) {
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
        GameEvent.BlockPlaceRequest.fire(tx, ty, selectedType);
    }

    private void selectingBlock(InputHandler input, int key, AssetType selectedType) {
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
