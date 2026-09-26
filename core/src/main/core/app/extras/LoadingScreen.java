package core.app.extras;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import core.app.Vars;
import core.event.Events;
import core.event.events.AppEvent;
import ui.Style;

public class LoadingScreen implements Disposable {

    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font;

    private static LoadingScreen instance;
    private static boolean initialized;

    public LoadingScreen() {

        //TODO: later make this as a global class/method to call when loading assets
        font = Style.getFont(Style.fonts.Aldrich);
    }

    public static LoadingScreen init() {
        if (initialized) return instance;
        initialized = true;

        Events.on(AppEvent.LoadingScreenEvent.class, e -> {
            if (instance == null) instance = new LoadingScreen();

            instance.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            instance.render(e.progress);
        });

        Events.on(AppEvent.GameLoaded.class, e -> closeScreen());

        return instance;
    }

    private static void closeScreen() {
        if (instance == null) return;
        instance.dispose();
        instance = null;
    }

    //TODO: add image/video to this
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
