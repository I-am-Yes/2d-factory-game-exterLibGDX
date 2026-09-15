package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import java.util.Objects;

public class UiHelper {

    public UiHelper() {}

    public Label createTextLabel(String text) {
        return createTextLabel(text, Style.getTextStyle(Style.textStyle.TEXT_STYLE_1), 100f, 20f, Align.center);
    }

    public Label createTextLabel(String text, Label.LabelStyle labelStyle) {
        return createTextLabel(text, labelStyle, 100f, 20f, Align.center);
    }

    public Label createTextLabel(String text, Label.LabelStyle labelStyle, float width, float height) {
        return createTextLabel(text, labelStyle, width, height, Align.center);
    }

    public Label createTextLabel(String text, Label.LabelStyle labelStyle, float width, float height, int alignment) {
        Label label = new Label(text, labelStyle);
        label.setStyle(labelStyle);
        label.setSize(width, height);
        label.setAlignment(alignment);
        return label;
    }

    public Window createClosablePanel(Skin skin) {
        Window closablePanel = configsPanel(null, skin);
        addCloseButton(closablePanel, skin);

        return closablePanel;
    }

    public TextButton addCloseButton(Window panel, Skin skin) {
        TextButton closeButton = createCloseButton(skin, Style.fonts.Aldrich.getFont());
        float width = Math.min(panel.getWidth() / 5f, 45f);
        float height = Math.min(panel.getHeight() / 5f, 30f);
        panel.getTitleTable()
            .add(closeButton)
            .size(width, height)
            .padRight(-panel.getPadRight())
            .padTop((height * 1.0f) -panel.getPadTop());
        closeButton.setTouchable(Touchable.enabled);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                panel.setVisible(false);
            }
        });
        return closeButton;
    }

    public TextButton createCloseButton(Skin skin, BitmapFont font) {
        TextButton.TextButtonStyle closeStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        closeStyle.font = font;
        return new TextButton("X", closeStyle);
    }

    public TextButton createTextButton(Skin skin) {
        return new TextButton("yes", skin);
    }

    public Window createPanel(Skin skin) {
        return createPanel(null, skin);
    }

    public Window createPanel(String title, Skin skin) {
        return createPanel(title, skin, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
    }

    public Window configsPanel(Window panel, Skin skin) {
        return configsPanel(panel, skin, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
    }

    public Window createPanel(String title, Skin skin, float width, float height) {
        return createPanel(title, skin, width, height, 0f);
    }

    public Window configsPanel(Window panel, Skin skin, float width, float height) {
        return configsPanel(panel, skin, width, height, 0f);
    }

    public Window createPanel(String title, Skin skin, float width, float height, float pad) {
        return createPanel(title, skin, width, height, pad, null);
    }

    public Window configsPanel(Window panel, Skin skin, float width, float height, float pad) {
        return configsPanel(panel, skin, width, height, pad, null);
    }

    public Window createPanel(String title, Skin skin, float width, float height, float pad, Color backgroundColor) {
        return createPanel(title, skin, width, height, Gdx.graphics.getWidth() / 2f - width / 2f, Gdx.graphics.getHeight() / 2f - height / 2f, pad, backgroundColor, null);
    }

    public Window configsPanel(Window window, Skin skin, float width, float height, float posX, float posY, float pad, Color backgroundColor) {
        return configsPanel(window, skin, width, height, posX, posY, pad, backgroundColor, null);
    }

    public Window createPanel(String title, Skin skin, float width, float height, float posX, float posY, float pad, Color backgroundColor) {
        return createPanel(title, skin, width, height, posX, posY, pad, backgroundColor, null);
    }

    public Window configsPanel(Window panel, Skin skin, float width, float height, float pad, Color backgroundColor) {
        return configsPanel(panel, skin, width, height, Gdx.graphics.getWidth() / 2f - width / 2f, Gdx.graphics.getHeight() / 2f - height / 2f, pad, backgroundColor, null);
    }

    public Window createPanel(String title, Skin skin, float width, float height, float posX, float posY, float pad, Color backgroundColor, Drawable backgroundImage) {
        return createPanel(title, skin, Style.getFont(Style.fonts.Aldrich), width, height, posX, posY, pad, backgroundColor, backgroundImage);
    }

    public Window configsPanel(Window panel, Skin skin, float width, float height, float posX, float posY, float pad, Color backgroundColor, Drawable backgroundImage) {
        return configsPanel(panel, skin, Style.getFont(Style.fonts.Aldrich), width, height, posX, posY, pad, backgroundColor, backgroundImage);
    }

    public Window createPanel(String title, Skin skin, BitmapFont font, float width, float height, float posX, float posY, float pad, Color backgroundColor, Drawable backgroundImage) {
        return configsPanel(null, title, skin, font, width, height, posX, posY, pad, pad, pad, pad, backgroundColor, backgroundImage, false, false, false, false, true);
    }

    public Window configsPanel(Window panel, Skin skin, BitmapFont font, float width, float height, float posX, float posY, float pad, Color backgroundColor, Drawable backgroundImage) {
        if (panel == null) {
            return configsPanel(null, null, skin, font, width, height,
                posX, posY, pad, pad, pad, pad,
                backgroundColor, backgroundImage,
                false, false, false, false, true);
        }
        return configsPanel(panel, panel.getTitleLabel().getText().toString(),
            skin, font, width, height, posX, posY, pad, pad, pad, pad, backgroundColor,
            backgroundImage, false, false, false, false, true);
    }

    public Window configsPanel(
        Window panel, String title, Skin skin, BitmapFont font,
        float width, float height, float posX, float posY, float padTop, float padRight, float padBottom, float padLeft,
        Color backgroundColor, Drawable backgroundImage,
        boolean moveable, boolean resizable, boolean fillParent, boolean visible, boolean touchable
    ) {
        skin.get(Window.WindowStyle.class).titleFont = font;
        //If no window passed in, create new window
        Window thisWindow = Objects.requireNonNullElseGet(panel, () -> new Window(title, skin));

        String finalTitle = (title == null || title.isBlank())
            ? "You Forgot To Add Title"
            : title;
        thisWindow.getTitleLabel().setText(finalTitle);

        thisWindow.setSize(width, height);
        thisWindow.setPosition(posX, posY);
        if (backgroundColor != null) {
            thisWindow.setColor(backgroundColor);
        }
        if (backgroundImage != null) {
            thisWindow.setBackground(backgroundImage);
        }
        if (touchable) {
            thisWindow.setTouchable(Touchable.enabled);
        }

        if (padTop + padBottom + padLeft + padRight != 0) thisWindow.pad(padTop, padRight, padBottom, padLeft);
        thisWindow.setFillParent(fillParent);
        thisWindow.setMovable(moveable);
        thisWindow.setResizable(resizable);
        thisWindow.setVisible(visible);
        return thisWindow;
    }

}
