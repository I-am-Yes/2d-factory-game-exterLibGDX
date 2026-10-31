package core.utils.unit.units;

import com.badlogic.gdx.graphics.g2d.Sprite;

import arcane.graphics.*;
import core.utils.unit.*;

import static arcane.Cores.*;
import static arcane.input.Input.Keys.*;

public class PlayerUnit extends Unit {
    private final float PLAYER_WIDTH = DEFAULT_UNIT_SIZE;
    private final float PLAYER_HEIGHT = DEFAULT_UNIT_SIZE;
    private final float PLAYER_SPEED = 8f;

    public PlayerUnit() {
        setName(UnitType.PLAYER_UNIT.getName());
        sprite = new Sprite(UnitType.PLAYER_UNIT.getRegion());
        setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
        setSpeedMult(PLAYER_SPEED);

        rotateSpeed = 480f;
    }

    public void update() {
        movePlayerUpdate();
    }

    public void movePlayerUpdate() {
        controlMove2();

    }

    //TODO: make this multi-directional-driving
    private void controlMove2() {
        rotateSpeed = 30f;
        if (input.isKeyPressed(A)) moveAndDirTo(getX() - PLAYER_SPEED, getY(), 180f);
        if (input.isKeyPressed(D)) moveAndDirTo(getX() + PLAYER_SPEED, getY(), 0f);
        if (input.isKeyPressed(W)) moveAndDirTo(getX(), getY() + PLAYER_SPEED, 90f);
        if (input.isKeyPressed(S)) moveAndDirTo(getX(), getY() - PLAYER_SPEED, 270f);
    }

    private void controlMove() {
        if (input.isKeyPressed(A)) moveDir(180f);
        if (input.isKeyPressed(D)) moveDir(0f);
        if (input.isKeyPressed(W)) moveDir(90f);
        if (input.isKeyPressed(S)) moveDir(270f);
        dirTo(input.getMouseWorldPos().x, input.getMouseWorldPos().y);
    }

    public void draw() {
        Artist.DrawSprite(sprite);
    }


}
