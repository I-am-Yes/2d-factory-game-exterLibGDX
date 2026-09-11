package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
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

    public PlayerHotbar (Window window, Stage stage, Skin skin, Style style) {
        this.window = window;
        this.stage = stage;
        this.skin = skin;
        this.style = style;

        textStyle1 = style.getTextStyle(Style.textStyle.TEXT_STYLE_1);




        //create texture
//        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(hotbarSkin.getRegion("hotbar-normal"));
//        TextureRegionDrawable selectedDrawable = new TextureRegionDrawable(hotbarSkin.getRegion("hotbar-checked"));
//        TextureRegionDrawable borderDrawable = new TextureRegionDrawable(hotbarSkin.getRegion("hotbar-border"));
//
//        TextButton.TextButtonStyle hotbarStyle = new TextButton.TextButtonStyle();
//        hotbarStyle.up = normalDrawable;
//        hotbarStyle.down = selectedDrawable;
//        hotbarStyle.checked = borderDrawable;
//        hotbarStyle.font = font;
//        hotbarStyle.fontColor = Color.WHITE;

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
        hotbar.setBackground(skin.getDrawable("white"));
        hotbar.bottom();
        hotbar.setSize((float) Gdx.graphics.getWidth() / 2, (float) Gdx.graphics.getHeight() / 10);
        hotbar.setPosition((float) Gdx.graphics.getWidth() / 2 - hotbar.getWidth() / 2, 0);

        for (int i = 0; i < hotbarItemButtons.length; i++) {
            hotbarItemButtons[i] = new TextButton(String.valueOf(i + 1), skin);
            hotbarItemButtons[i].setBackground(skin.getDrawable("black"));
            hotbarItemButtons[i].setFillParent(false);
            hotbarItemGroup.add(hotbarItemButtons[i]);
            hotbar.add(hotbarItemButtons[i]).pad(1).size(50, 50);
        }

    }

    public void update() {}

}
