package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import core.AssetsHandler;
import core.InputHandler;
import core.Window;
import data.map.asset.AssetType;
import data.map.asset.BuildingType;
import data.map.asset.FloorType;
import data.map.asset.GhostType;

import java.util.Arrays;

public class PlayerHotbar {
    private final Window window;
    private final Stage stage;
    private final Skin skin;
    private final Table hotbar;
    private final Label selectionLabel;
    private final TextButton[] hotbarItemButtons;
    private final ButtonGroup<TextButton> hotbarItemGroup;

    private final InterfaceAction UIaction;

    private final Label.LabelStyle textStyle1;
    private final BitmapFont aldrichFont;
    private final Image selectionBorder;

    private final TextureAtlas hotbarSkin;
    private final TextureAtlas tilesAtlas;
    private final InputHandler input;
    private final AssetsHandler assets;

    private final Image[] hotbarItemIcons;

    private final float ICON_CONTAINER_PADDING = 6f;

    private static final int HOTBAR_SLOT_COUNT = 9;

    private static final int[] HOTBAR_KEYS = {
        Input.Keys.NUM_1, Input.Keys.NUM_2, Input.Keys.NUM_3,
        Input.Keys.NUM_4, Input.Keys.NUM_5, Input.Keys.NUM_6,
        Input.Keys.NUM_7, Input.Keys.NUM_8, Input.Keys.NUM_9
    };

    private static final AssetType[] HOTBAR_ASSETS = new AssetType[HOTBAR_SLOT_COUNT];

    public static AssetType selectedType;

    public PlayerHotbar(Window window, Stage stage, Skin skin, InterfaceAction UiAction, BitmapFont aldrichFont, InputHandler input, AssetsHandler assets) {
        this.window = window;
        this.stage = stage;
        this.skin = skin;
        this.aldrichFont = aldrichFont;
        this.input = input;
        this.UIaction = UiAction;
        this.assets = assets;

        //TODO: later add text label to show item name and description
        //TODO: later add text label on top of hotbar to temporary show selected item name

        this.tilesAtlas = assets.getTilesAtlas();

        textStyle1 = Style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        hotbarSkin = new TextureAtlas(Gdx.files.internal("packed/ui/hotbar/hotbar assets.atlas"));

        TextureRegionDrawable hotbarBox = new TextureRegionDrawable(hotbarSkin.findRegion("box background"));
        TextureRegionDrawable hotbarBoxSelected = new TextureRegionDrawable(hotbarSkin.findRegion("box selected"));
        TextureRegionDrawable hotbarBoxBorder = new TextureRegionDrawable(hotbarSkin.findRegion("box border center doubled"));

        TextButton.TextButtonStyle hotbarStyle = new TextButton.TextButtonStyle();
        hotbarStyle.up = hotbarBox;
        hotbarStyle.down = hotbarBoxSelected;
        hotbarStyle.checked = hotbarBoxSelected;
//        hotbarStyle.checkedDown = hotbarBoxSelected;

        hotbarStyle.over = hotbarBoxSelected;

        hotbarStyle.font = aldrichFont;
        hotbarStyle.fontColor = Color.WHITE;

        selectionBorder = new Image(hotbarBoxBorder);
        selectionBorder.setTouchable(Touchable.disabled);
        selectionBorder.setVisible(false);

        //create actors
        selectionLabel = new Label(null, textStyle1);

        hotbar = new Table();
        hotbarItemButtons = new TextButton[HOTBAR_SLOT_COUNT];
        hotbarItemIcons = new Image[HOTBAR_SLOT_COUNT];

        hotbarItemGroup = new ButtonGroup<>();

        hotbar.setFillParent(false);
        hotbarItemGroup.setMinCheckCount(0);
        hotbarItemGroup.setMaxCheckCount(1);

        //add actors to stage
        stage.addActor(hotbar);
        stage.addActor(selectionLabel);
        stage.addActor(selectionBorder);


        //config actors
        hotbar.setTouchable(Touchable.childrenOnly);
        hotbar.setOrigin(Align.center, Align.center);
        hotbar.bottom();
        hotbar.setSize((float) window.getWindowWidth() / 2, (float) window.getWindowHeight() / 10);
        hotbar.setPosition((float) window.getWindowWidth() / 2 - hotbar.getWidth() / 2, (float) window.getWindowHeight() / 100);

        for (int i = 0; i < hotbarItemButtons.length; i++) {
            TextButton button = new TextButton(
                String.valueOf(i + 1),
                hotbarStyle
            );

            Image icon = new Image();
            icon.setScaling(Scaling.fit);
            icon.setTouchable(Touchable.disabled);

            Container<Image> iconContainer = new Container<>(icon);
            iconContainer.fill();
            iconContainer.pad(ICON_CONTAINER_PADDING);
            iconContainer.setTouchable(Touchable.disabled);

            Stack slot = new Stack();
            slot.add(button);
            slot.add(iconContainer);

            final boolean[] hovering = { false };

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!button.isChecked()) {
                        selectionBorder.clearActions();
                        if (selectionBorder.isVisible()) {
                            selectionBorder.addAction(
                                Actions.sequence(
                                    Actions.fadeOut(0.1f),
                                    Actions.hide()
                                )
                            );
                        }
                        return;
                    }

                    Vector2 borderTarget = slot.localToStageCoordinates(
                        new Vector2(0f, 0f)
                    );

                    selectionBorder.clearActions();
                    selectionBorder.setSize(slot.getWidth(), slot.getHeight());
                    selectionBorder.setVisible(true);
                    selectionBorder.getColor().a = 1f;
                    selectionBorder.toFront();

                    float borderY = borderTarget.y + (hovering[0] ? 4f : 0f);

                    selectionBorder.addAction(
                        Actions.moveTo(borderTarget.x, borderY, 0.1f, Interpolation.smoother)
                    );

                    selectionBorder.setOrigin(Align.center);
                    selectionBorder.addAction(
                        Actions.scaleTo(1.08f, 1.08f, 0.1f, Interpolation.smooth2)
                    );
                }

            });

            float generalDuration = 0.08f;


            UiAction.addHoverEffect(button,
                () -> Actions.moveTo(0f, 4f, generalDuration),
                () -> Actions.moveTo(0f, 0f, generalDuration),
                () -> {
                    hovering[0] = true;

                    iconContainer.addAction(Actions.moveTo(0f, 4f, generalDuration));

                    if (button.isChecked()) {

                        Vector2 target = slot.localToStageCoordinates(new Vector2(0f, 0f));

                        UiAction.moveTo(selectionBorder, target.x, target.y + 4f, generalDuration);
                    }
                },
                () -> {
                    hovering[0] = false;

                    iconContainer.addAction(Actions.moveTo(0f, 0f, generalDuration));

                    if (button.isChecked()) {
                        Vector2 target = slot.localToStageCoordinates(new Vector2(0f, 0f));

                        UiAction.moveTo(selectionBorder, target.x, target.y, generalDuration);
                    }
                }
            );

            hotbarItemButtons[i] = button;
            hotbarItemIcons[i] = icon;
            hotbarItemGroup.add(button);
            hotbar.add(slot).pad(1).size(50, 50);
        }

        //this set pos to prevent the border appearing from somewhere else before animating
        //TODO: later improve this to make the border appear from the selected slot, not the center of the hotbar or other previous selected slot if there is one
        // note: the purpose of this animation is to smooth move the border from previous selected slot to current,
        // so if the current previous selected slot is null, it spawn from new selected slot.
        selectionBorder.setPosition(hotbar.getX() + hotbar.getWidth() / 2f, hotbar.getY());

        selectionLabel.setVisible(false);
        selectionLabel.setPosition(Gdx.graphics.getWidth() / 2f, hotbar.getY() + hotbar.getHeight());
        selectionLabel.setAlignment(Align.center);


        setHotbarItems(
            1, BuildingType.HAZARD_BLOCK,
            2, BuildingType.HAZARD_BLOCK2,
            3, FloorType.SAND,
            4, FloorType.STONE,
            5, FloorType.MARBLE,
            6, FloorType.WATER
        );

//        System.out.println(Arrays.toString(getAllHotbarItems()));

    }

    public void update() {

        updateInputToHotbar();
        updateSelectedType();

    }

    public void dispose() {
        hotbarSkin.dispose();
    }

    private void updateSelectionLabel(AssetType type) {
        selectionLabel.clearActions();
        if (type == null) {
            selectionLabel.setText(null);
            selectionLabel.setVisible(false);
            return;
        }

        selectionLabel.setText(type.getNamePNG());
        selectionLabel.getColor().a = 0f;
        selectionLabel.setVisible(true);
        selectionLabel.addAction(Actions.sequence(
            Actions.fadeIn(0.15f),
            Actions.delay(1.5f),
            Actions.fadeOut(0.3f),
            Actions.hide()
        ));
    }

    private void updateSelectedType() {
        int selectedIndex = hotbarItemGroup.getCheckedIndex();

        if (selectedType == getHotbarItem(selectedIndex)) return;

        selectedType = getHotbarItem(selectedIndex);
        updateSelectionLabel(selectedType);
    }

    private void updateInputToHotbar() {
        for (int i = 0; i < HOTBAR_KEYS.length; i++) {
            if (input.isKeyJustPressed(HOTBAR_KEYS[i])) {
                hotbarItemButtons[i].setChecked(!hotbarItemButtons[i].isChecked());
                updateSelectedType();
                break;
            }
        }
    }

    public void setHotbarItems(Object... values) {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException(
                "Expected slot/type pairs."
            );
        }

        for (int i = 0; i < values.length; i += 2) {
            if (!(values[i] instanceof Integer slotNumber)) {
                throw new IllegalArgumentException(
                    "Expected an integer slot number."
                );
            }
            if (!(values[i + 1] instanceof AssetType type)) {
                throw new IllegalArgumentException(
                    "Expected an AssetType."
                );
            }
            setHotbarItem(slotNumber - 1, type);
        }
    }

    private void setHotbarItem(int index, AssetType type) {
        if (index < 0 || index >= hotbarItemButtons.length) return;
        TextureRegionDrawable icon;
        if (type != null) {
            TiledMapTile tile = assets.getTile(type);

            if (tile != null) {
                icon = new TextureRegionDrawable(tile.getTextureRegion());
                hotbarItemIcons[index].setDrawable(icon);
                HOTBAR_ASSETS[index] = type;
            }
        }
    }

    public static AssetType[] getAllHotbarItems() {
        return HOTBAR_ASSETS.clone();
    }

    public AssetType getHotbarItem(int index) {
        if (index < 0 || index >= hotbarItemButtons.length) return null;
        return HOTBAR_ASSETS[index];
    }

    public static AssetType getSelectedType() {
        return selectedType;
    }

}
