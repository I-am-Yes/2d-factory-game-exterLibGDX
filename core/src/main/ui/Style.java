package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Null;

public class Style {

    public static int QUALITY_MULTIPLIER = 32;
    public static int FONT_SIZE = 16;
    public static int QUALIFIED_FONT_SIZE = FONT_SIZE * QUALITY_MULTIPLIER;
    public static float DEFAULT_FONT_SCALE = 1f / QUALITY_MULTIPLIER;
    public static Color DEFAULT_FONT_COLOR = Color.WHITE;

    public enum textStyle {
        TEXT_STYLE_1(new Label.LabelStyle(createFont(fonts.Aldrich, QUALIFIED_FONT_SIZE, DEFAULT_FONT_SCALE, Color.WHITE), Color.WHITE)),
        //add more preset style there

        ;

        private final Label.LabelStyle labelStyle;

        textStyle(Label.LabelStyle labelStyle) {
            this.labelStyle = labelStyle;
        }
        public Label.LabelStyle getStyle() {
            return labelStyle;
        }
    }

    public static Label.LabelStyle getTextStyle(textStyle textStyle) {
        return textStyle.getStyle();
    }

    public enum fonts {
        Aldrich("ui/fonts/Aldrich/Aldrich-Regular.ttf"),
        //add more font there

        ;

        private final String fontPath;

        fonts(String fontPath) {
            this.fontPath = fontPath;
        }

        public String getFontPath() {
            return fontPath;
        }
        public BitmapFont getFont() {
            return createFont(this);
        }
        public BitmapFont getFont(int fontSize, Color fontColor) {
            return createFont(this, fontSize, null, fontColor);
        }
        public BitmapFont getFont(int fontSize, float fontScale, Color fontColor) {
            return createFont(this, fontSize, fontScale, fontColor);
        }
    }

    public static BitmapFont createFont(fonts font, @Null Integer fontSize, @Null Float fontScale, @Null Color fontColor) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(font.getFontPath()));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        if (fontSize != null && fontSize != 0) parameter.size = fontSize;
        else parameter.size = QUALIFIED_FONT_SIZE;

        if (fontColor != null) parameter.color = fontColor;
        else parameter.color = DEFAULT_FONT_COLOR;

        BitmapFont newFont = generator.generateFont(parameter);

        if (fontScale != null && fontScale != 0) newFont.getData().setScale(fontScale);
        else newFont.getData().setScale(DEFAULT_FONT_SCALE);

        newFont.getRegion().getTexture().setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        generator.dispose();
        return newFont;
    }

    public static BitmapFont createFont(fonts fonts) {
        return createFont(fonts, null, null, null);
    }

    public Label.LabelStyle createStyle(fonts font, int fontSize, float fontScale, Color fontColor) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = createFont(font, fontSize, fontScale, fontColor);
        return style;
    }

}
