package core.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import core.player.mechanic.PlayerController;
import data.PlayerData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import core.world.World;

public class Player {
    private final float PLAYER_WIDTH = PlayerData.getPlayerWidth();
    private final float PLAYER_HEIGHT = PlayerData.getPlayerHeight();
    private final float PLAYER_SPEED = PlayerData.getPlayerSpeed();

    private final World world;
    private final Sprite sprite;
    private final PlayerController controller;
    private final SpriteBatch spriteBatch;

    private final float worldWidth;
    private final float worldHeight;

    //TODO: change this player texture to a regis system.
    private final Texture playerTexture = new Texture("unpacked/player/player.png");

    public Player(World world, PlayerController controller, SpriteBatch playerSpriteBatch) {
        this.controller = controller;
        this.world = world;
        this.worldWidth = world.getWorldWidth();
        this.worldHeight = world.getWorldHeight();
        this.spriteBatch = playerSpriteBatch;

        sprite = new Sprite(playerTexture);
        sprite.setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
        sprite.setPosition(worldWidth / 2f, worldHeight / 2f);  // spawn in middle of the map
    }

    public void update(float delta) {
        controller.moveCharacter(getPlayer(), sprite, delta, PLAYER_SPEED, worldWidth, worldHeight);
    }

    public void draw() {
        sprite.draw(spriteBatch);
    }

    public PlayerController getController() {
        return controller;
    }

    public Player getPlayer() {
        return this;
    }

    public Sprite getSprite() {
        return sprite;
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

    public Texture getPlayerTexture() {
        return playerTexture;
    }

}
