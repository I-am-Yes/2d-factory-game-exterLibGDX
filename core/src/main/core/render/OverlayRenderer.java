package core.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.World;

public class OverlayRenderer {

    private final Vector3 tmp = new Vector3();

    public void render(World world, Viewport viewport, ShapeRenderer shapeRenderer) {

        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();

        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(tmp);

        int tx = world.worldToTileX((int) tmp.x);
        int ty = world.worldToTileY((int) tmp.y);

        if (!world.isInBounds(tx, ty)) return;

        float tileSize = world.getTileSize();
        float wx = tx * tileSize;
        float wy = ty * tileSize;

        boolean walkAble = world.isWalkable(tx, ty);

        // walkAble = light green, not walkAble = red,
        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.gl.glLineWidth(0.1f);

        //begin
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (walkAble) {
            shapeRenderer.setColor(Color.CLEAR_WHITE);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        shapeRenderer.rect(wx, wy, tileSize, tileSize);

        shapeRenderer.end();

    }

}
