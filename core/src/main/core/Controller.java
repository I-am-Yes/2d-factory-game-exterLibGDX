package core;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Controller {

    public void moveCharacter(Sprite sprite, float delta, float speed, float worldW, float worldH, InputHandler input) {
        if (input.isKeyPressed(Input.Keys.W, Input.Keys.UP))  sprite.translateY(speed * delta);
        if (input.isKeyPressed(Input.Keys.S, Input.Keys.DOWN)) sprite.translateY(-speed * delta);
        if (input.isKeyPressed(Input.Keys.A, Input.Keys.LEFT))  sprite.translateX(-speed * delta);
        if (input.isKeyPressed(Input.Keys.D, Input.Keys.RIGHT)) sprite.translateX(speed * delta);

        sprite.setX(MathUtils.clamp(sprite.getX(), 0f, worldW - sprite.getWidth()));
        sprite.setY(MathUtils.clamp(sprite.getY(), 0f, worldH - sprite.getHeight()));


    }

}
