package core;

import Data.PlayerData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class player {
    private float PLAYER_WIDTH = PlayerData.PLAYER_WIDTH;
    private float PLAYER_HEIGHT = PlayerData.PLAYER_HEIGHT;
    private float PLAYER_SPEED = PlayerData.PLAYER_SPEED;

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
