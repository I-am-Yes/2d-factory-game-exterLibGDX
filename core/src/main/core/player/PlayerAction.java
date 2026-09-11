package core.player;

import com.badlogic.gdx.math.Vector2;
import core.app.context.GameContext;
import core.player.mechanic.OverlayHelper;
import core.system.PlayerSystem;
import data.map.asset.AssetType;
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
import core.entities.BuildGhostLine;
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

    private final Vector3 tmp = new Vector3();
    private final Vector2 hoverTile = new Vector2();

    private AssetType selectedType;

    public enum PlaceMode {
        none, ghost, placing, breaking,
    }
    private PlaceMode placeMode = PlaceMode.none;
    private int selectX = -1, selectY = -1;

    private float delta;

    public PlayerAction(GameContext context, Player player) {
        this.world = context.world;
        this.player = player;
        this.input = context.input;
        this.viewport = context.viewport;
        this.shapeRenderer = context.shapeRenderer;
        this.spriteBatch = context.spriteBatch;
        this.assets = context.assets;

        this.delta = context.getDeltaTime();
        this.buildGhostLine = new BuildGhostLine(world, player, viewport, this, input, spriteBatch, shapeRenderer, assets);
    }

    public void update(float delta) {
        this.delta = delta;

        hoverTile.set(
            PlayerSystem.getHoverTileX(),
            PlayerSystem.getHoverTileY()
        );

        buildGhostLine.update(getSelectedType());

        if (input.isKeyJustPressed(NUM_1)) clearSelection();
        selectingBlock(input, NUM_2, FloorType.SAND);
        selectingBlock(input, NUM_3, FloorType.STONE);
        selectingBlock(input, NUM_4, FloorType.ROCK);

        if (placeMode == PlaceMode.ghost) {
            return;
        }
        if (selectedType == null) return; //block placing when selectedType = null

        if (input.isMousePressed(Input.Buttons.MIDDLE)) return; //panning = no place block

//        int tx = screenToTileX(viewport, world);
//        int ty = screenToTileY(viewport, world);

        int tx = (int) hoverTile.x;
        int ty = (int) hoverTile.y;

        if (!world.isInBounds(tx, ty)) {
            placeMode = PlaceMode.none;
            return;
        }

        //handle mouse press, start placement
        if (input.isMouseJustPressed(Input.Buttons.LEFT) && selectedType != null) {
            selectX = tx;
            selectY = ty;
            placeMode = PlaceMode.placing;
            GameEvent.BlockPlaceRequest.fire(selectX, selectY, selectedType);

        }

        //handle mouse drag, update line
        if (input.isMousePressed(Input.Buttons.LEFT) && placeMode == PlaceMode.placing) {
            selectX = tx;
            selectY = ty;
            GameEvent.BlockPlaceRequest.fire(selectX, selectY, selectedType);
        }

        //handle mouse release, end placement
        if (input.isMouseReleased(Input.Buttons.LEFT) && placeMode == PlaceMode.placing) {

            placeMode = PlaceMode.none;
        }

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
    public SpriteBatch getSpriteBatch() {
        return this.spriteBatch;
    }
}
