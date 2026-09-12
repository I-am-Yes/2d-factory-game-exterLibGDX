package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import core.InputHandler;
import core.Window;

public class PlayerHotbar {
    private final Window window;
    private final Stage stage;
    private final Skin skin;
    private final Style style;
    private final Table hotbar;
    private final Label textLabel;
    private final TextButton[] hotbarItemButtons;
    private final ButtonGroup<TextButton> hotbarItemGroup;

    private final Label.LabelStyle textStyle1;
    private final BitmapFont aldrichFont;

    private final TextureAtlas hotbarSkin;
    private final InputHandler input;

    public PlayerHotbar(Window window, Stage stage, Skin skin, Style style, BitmapFont aldrichFont, InputHandler input) {
        this.window = window;
        this.stage = stage;
        this.skin = skin;
        this.style = style;
        this.aldrichFont = aldrichFont;
        this.input = input;

        textStyle1 = style.getTextStyle(Style.textStyle.TEXT_STYLE_1);

        hotbarSkin = new TextureAtlas(Gdx.files.internal("packed/ui/hotbar/hotbar assets.atlas"));

        TextureRegionDrawable hotbarBox = new TextureRegionDrawable(hotbarSkin.findRegion("hotbar box"));
        TextureRegionDrawable hotbarBoxSelected = new TextureRegionDrawable(hotbarSkin.findRegion("hotbar box selected"));
        TextureRegionDrawable hotbarBoxBorder = new TextureRegionDrawable(hotbarSkin.findRegion("hotbar border"));

        TextButton.TextButtonStyle hotbarStyle = new TextButton.TextButtonStyle();
        hotbarStyle.up = hotbarBox;
        hotbarStyle.down = hotbarBoxSelected;
        hotbarStyle.checked = hotbarBoxSelected;
        hotbarStyle.checkedDown = hotbarBoxSelected;

        hotbarStyle.font = aldrichFont;
        hotbarStyle.fontColor = Color.WHITE;

        //create actors
        textLabel = new Label("text label", textStyle1);

        hotbar = new Table();
        hotbarItemButtons = new TextButton[9];
        hotbarItemGroup = new ButtonGroup<>();

        hotbar.setFillParent(false);
        hotbarItemGroup.setMinCheckCount(0);
        hotbarItemGroup.setMaxCheckCount(1);

        //add actors to stage
        stage.addActor(hotbar);
        stage.addActor(textLabel);

        //config actors
        textLabel.setText("text label");
        textLabel.setAlignment(Align.center);


        hotbar.setOrigin(Align.center, Align.center);
        hotbar.bottom();
        hotbar.setSize((float) window.getWindowWidth() / 2, (float) window.getWindowHeight() / 10);
        hotbar.setPosition((float) window.getWindowWidth() / 2 - hotbar.getWidth() / 2, 0);

        for (int i = 0; i < hotbarItemButtons.length; i++) {
            TextButton button = new TextButton(
                String.valueOf(i + 1),
                hotbarStyle
            );

            Image border = new Image(hotbarBoxBorder);
            border.setTouchable(Touchable.disabled);
            border.setVisible(false);

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    border.setVisible(button.isChecked());

                    button.setTransform(true);
                    button.addAction(
                        Actions.sequence(
                            Actions.scaleTo(0.9f, 0.9f, 0.05f),
                            Actions.scaleTo(1f, 1f, 0.10f)
                        )
                    );
                }

            });

            button.setTransform(true);
            button.setOrigin(Align.center);

            button.addListener(new InputListener() {
                @Override
                public void enter(
                    InputEvent event,
                    float x,
                    float y,
                    int pointer,
                    Actor fromActor
                ) {
                    if (pointer == -1) { // real mouse, not a touch pointer
                        button.clearActions();
                        button.addAction(Actions.scaleTo(1.08f, 1.08f, 0.10f));
                    }
                }

                @Override
                public void exit(
                    InputEvent event,
                    float x,
                    float y,
                    int pointer,
                    Actor toActor
                ) {
                    if (pointer == -1) {
                        button.clearActions();
                        button.addAction(Actions.scaleTo(1f, 1f, 0.10f));
                    }
                }
            });



            Stack slot = new Stack();
            slot.add(button);
            slot.add(border);

            hotbarItemButtons[i] = button;
            hotbarItemGroup.add(button);
            hotbar.add(slot).pad(1).size(50, 50);
        }

    }

    public void update() {
    }

    public void dispose() {
        hotbarSkin.dispose();
    }

}
