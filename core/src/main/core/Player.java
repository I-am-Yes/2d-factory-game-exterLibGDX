package core;

import Data.PlayerData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import core.controller.Controller;
import core.controller.PlayerAction;
import core.world.World;

public class Player {
    private final float PLAYER_WIDTH = PlayerData.getPlayerWidth();
    private final float PLAYER_HEIGHT = PlayerData.getPlayerHeight();
    private final float PLAYER_SPEED = PlayerData.getPlayerSpeed();



    public final Sprite sprite;

    private final World world;
    private final Controller controller;

    public Player(Texture texture, World world, float worldWidth, float worldHeight, Controller controller, AssetsHandler assets) {
        this.controller = controller;
        this.world = world;

        sprite = new Sprite(texture);
        sprite.setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
        sprite.setPosition(worldWidth / 2f, worldHeight / 2f);  // spawn in middle of the map
    }

    public void update(float delta, float WorldWidth, float worldHeight, InputHandler input) {
        controller.moveCharacter(getPlayer(), sprite, delta, PLAYER_SPEED, WorldWidth, worldHeight);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Controller getController() {
        return controller;
    }

    public Player getPlayer() {
        return this;
    }

    public boolean isPlayerMoving() {
        return controller.isPlayerMoving();
    }

    public float getPlayerSpeed() {
        return PLAYER_SPEED;
    }

    public Vector2 getPos() {
        return new Vector2(sprite.getX(), sprite.getY());
    }

    public float getPlayerPositionX() {
        return sprite.getX();
    }
    public float getPlayerPositionY() {
        return sprite.getY();
    }

}
