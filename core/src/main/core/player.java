package core;

import Data.PlayerData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import core.controller.Controller;

public class player {
    private final float PLAYER_WIDTH = PlayerData.getPlayerWidth();
    private final float PLAYER_HEIGHT = PlayerData.getPlayerHeight();
    private final float PLAYER_SPEED = PlayerData.getPlayerSpeed();

    public final Sprite sprite;

    private Controller controller;

    public player(Texture texture, float worldWidth, float worldHeight, Controller controller) {
        this.controller = new Controller();

        sprite = new Sprite(texture);
        sprite.setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
        sprite.setPosition(worldWidth / 2f, worldHeight / 2f);  // spawn in middle of the map
    }

    public void update(float delta, float WorldWidth, float worldHeight, InputHandler input) {
        controller.moveCharacter(sprite, delta, PLAYER_SPEED, WorldWidth, worldHeight, input);

    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }


}
