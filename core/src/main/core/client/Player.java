package core.client;

import arcane.math.*;
import arcane.graphics.*;

import core.assets.data.PlayerData;
import core.client.player.*;
import core.client.player.mechanic.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;

import static core.app.Vars.*;

public class Player {
    private final float PLAYER_WIDTH = PlayerData.getPlayerWidth();
    private final float PLAYER_HEIGHT = PlayerData.getPlayerHeight();
    private final float PLAYER_SPEED = PlayerData.getPlayerSpeed();

    private final PlayerController controller;

    public static Sprite sprite;

    //TODO: change this player texture to a regis system.
    private final Texture playerTexture = new Texture("unpacked/player/player.png");

    private final PlayerAction action;

    public Player(PlayerController controller, PlayerAction action) {
        this.controller = controller;
        this.action = action;

        sprite = new Sprite(playerTexture);
        sprite.setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
        sprite.setPosition(2f, 2f);  // spawn in middle of the map
    }

    public void update() {
        controller.moveCharacter(getPlayer(), sprite, PLAYER_SPEED,
            world.getWorldWidth(), world.getWorldHeight());
    }

    public void draw() {
        Artist.DrawSprite(sprite, sprite.getX(), sprite.getY(), PLAYER_WIDTH, PLAYER_HEIGHT);
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

    public PlayerAction getAction() {
        return action;
    }

}
