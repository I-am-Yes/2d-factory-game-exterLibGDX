package core.render;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class AnimateRenderer {

    public static boolean animateMove(float delta, float endX, float endY,
                                      float speed, Vector2 anim, boolean initialized) {
        if (!initialized) {
            anim.set(endX, endY);
            return true;
        }
        float smooth = 1f - (float) Math.exp(-speed * delta);
        anim.x = MathUtils.lerp(anim.x, endX, smooth);
        anim.y = MathUtils.lerp(anim.y, endY, smooth);
        if (Math.abs(anim.x - endX) < 0.001f) anim.x = endX;
        if (Math.abs(anim.y - endY) < 0.001f) anim.y = endY;
        return true;
    }

}
