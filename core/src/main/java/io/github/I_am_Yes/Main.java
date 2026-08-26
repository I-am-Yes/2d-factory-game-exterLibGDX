package io.github.I_am_Yes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    @Override
    public void create() {
        Gdx.graphics.setVSync(false);
        Gdx.graphics.setForegroundFPS(0);

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();

        System.out.println(com.badlogic.gdx.Gdx.graphics.getFramesPerSecond());
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
