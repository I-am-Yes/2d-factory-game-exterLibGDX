package core.app;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import ui.Style;

public class LoadingScreen {

    private final SpriteBatch batch;
    private final BitmapFont font;

    public LoadingScreen() {

        //TODO: later make this as a global class/method to call when loading assets
        batch = new SpriteBatch();
        font = Style.getFont(Style.fonts.Aldrich);
    }

    public void render(float progress) {
        ScreenUtils.clear(Color.BLACK);

        //TODO: later add tip & trick text
        batch.begin();
        font.getData().setScale(0.1f);
        font.draw(batch, "Loading... " + (int)(progress * 100) + "%", 40, 80);
        font.getData().setScale(1.0f);
        batch.end();
    }

    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }


}
