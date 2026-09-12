package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Style {

    public enum textStyle {
        TEXT_STYLE_1(new Label.LabelStyle(createFont(fonts.Aldrich, 16, Color.WHITE), Color.WHITE)),
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

    public Label.LabelStyle getTextStyle(textStyle textStyle) {
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
            return createFont(this, fontSize, fontColor);
        }
    }

    public static BitmapFont createFont(fonts fonts, int fontSize, Color fontColor) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fonts.getFontPath()));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        parameter.color = fontColor;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    public static BitmapFont createFont(fonts fonts) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fonts.getFontPath()));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    public Label.LabelStyle createStyle(BitmapFont font, Color fontColor) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        style.fontColor = fontColor;
        return style;
    }

}
