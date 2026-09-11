package core.player.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.system.PlayerSystem;

public class OverlayHelper {

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

    public static void updateMouseWorld(Viewport viewport, Vector3 outMouseWorld) {
        outMouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(outMouseWorld);
    }

    public static void updateTileWithThreshold(
        Vector3 mouseWorld,
        float tileSize,
        Vector2 targetTile) {

        float threshold = PlayerSystem.HOVER_THRESHOLD;

        while (mouseWorld.x >= (targetTile.x + 1f + threshold) * tileSize) {
            targetTile.x++;
        }

        while (mouseWorld.x < (targetTile.x - threshold) * tileSize) {
            targetTile.x--;
        }

        while (mouseWorld.y >= (targetTile.y + 1f + threshold) * tileSize) {
            targetTile.y++;
        }

        while (mouseWorld.y < (targetTile.y - threshold) * tileSize) {
            targetTile.y--;
        }
    }

}
