package core;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;

public class Player {
    private static final float PLAYER_WIDTH = 2f;
    private static final float PLAYER_HEIGHT = 2f;
    private static final float PLAYER_SPEED = 4f;

    private final Sprite sprite;


    public Player(Texture texture, float worldWidth, float worldHeight) {
        sprite = new Sprite(texture);
        sprite.setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
        sprite.setPosition(worldWidth / 2f, worldHeight / 2f);  // spawn in middle of the map
    }

    public void update(float delta, float worldW, float worldH) {
        if (isKeyPressed(Input.Keys.W, Input.Keys.UP))  sprite.translateY(PLAYER_SPEED * delta);
        if (isKeyPressed(Input.Keys.S, Input.Keys.DOWN)) sprite.translateY(-PLAYER_SPEED * delta);
        if (isKeyPressed(Input.Keys.A, Input.Keys.LEFT))  sprite.translateX(-PLAYER_SPEED * delta);
        if (isKeyPressed(Input.Keys.D, Input.Keys.RIGHT)) sprite.translateX(PLAYER_SPEED * delta);

        sprite.setX(MathUtils.clamp(sprite.getX(), 0f, worldW - sprite.getWidth()));
        sprite.setY(MathUtils.clamp(sprite.getY(), 0f, worldH - sprite.getHeight()));
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    private boolean isKeyPressed(int... keys) {
        for (int key : keys) {
            if (Gdx.input.isKeyPressed(key)) return true;
        }
        return false;
    }
}
