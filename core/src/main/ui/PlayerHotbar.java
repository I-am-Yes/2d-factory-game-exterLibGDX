package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.Window;
import core.World;
import core.controller.PlayerAction;

public class PlayerHotbar {

    private final Stage stage;
    private final Skin skin;
    private final BitmapFont font;
    private final Table hotbar;
    private final Label textLabel;
    private final TextButton[] hotbarItemButtons;
    private final ButtonGroup<TextButton> hotbarItemGroup;

    public PlayerHotbar (Window window, World world, PlayerAction playerAction) {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new FitViewport(1280, 720));

        //create font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/fonts/Aldrich/Aldrich-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        //create style
        Label.LabelStyle textStyle = new Label.LabelStyle();
        textStyle.font = font;
        textStyle.fontColor = Color.WHITE;

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
        textLabel = new Label("text label", textStyle);
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

    public void update(float delta) {
        act(delta);
    }

    public void act(float delta) {
        stage.act(delta);
    }

    public void resize(int width, int height) {
//        hotbar.setSize((float) width / 2, (float) height / 10);
//        hotbar.setPosition((float) width / 2 - hotbar.getWidth() / 2, 0);
//
//        for (int i = 0; i < hotbarItemButtons.length; i++) {
//            hotbarItemButtons[i].setSize(50, 50);
//            hotbarItemButtons[i].setPosition((float) width / 2 - hotbar.getWidth() / 2 + i * 40, 0);
//        }

        stage.getViewport().update(width, height, false);
    }

    public void draw() {
        stage.draw();
    }

    public void dispose() {
        font.dispose();
        skin.dispose();
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }


}
