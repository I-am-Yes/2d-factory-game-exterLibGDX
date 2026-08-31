package core.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import core.World;

public class OverlayRenderer {

    private final Vector3 tmp = new Vector3();

    private float cornerBracketThickness = 3.5f;
    private float cornerBracketGapRatio = 0.27f;

    public void render(World world, Viewport viewport, ShapeRenderer shapeRenderer) {

        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        drawCornerBrackets(world, viewport, shapeRenderer, cornerBracketGapRatio, cornerBracketThickness);

        shapeRenderer.end();
    }

    private void drawCornerBrackets(World world, Viewport viewport, ShapeRenderer shapeRenderer,
                                    float gapRatio, float thicknessPx) {

        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();

        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(tmp);

        float tileSize = world.getTileSize();
        int tx = MathUtils.floor(tmp.x / tileSize);
        int ty = MathUtils.floor(tmp.y / tileSize);

        if (!world.isInBounds(tx, ty)) return;

        float wx = tx * tileSize;
        float wy = ty * tileSize;
        boolean walkAble = world.isWalkable(tx, ty);

        float gap = tileSize * gapRatio;
        float pad = 0.02f;

        float worldPerPixel = (camera.viewportWidth * camera.zoom) / Gdx.graphics.getWidth();
        float thickness = thicknessPx * worldPerPixel;

        float x = wx - pad;
        float y = wy - pad;
        float size = tileSize + pad * 2f;

        drawBracketShape(shapeRenderer, x, y, size, gap, thickness, walkAble);
    }

    private void drawBracketShape(ShapeRenderer shapeRenderer, float x, float y, float size,
                                  float gap, float thickness, boolean walkAble) {

        float x2 = x + size;
        float y2 = y + size;

        if (walkAble) {
            shapeRenderer.setColor(Color.WHITE);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        // top-left
        shapeRenderer.rect(x,     y2 - thickness, gap, thickness);
        shapeRenderer.rect(x,     y2 - gap,       thickness, gap);

        // top-right
        shapeRenderer.rect(x2 - gap, y2 - thickness, gap, thickness);
        shapeRenderer.rect(x2 - thickness, y2 - gap, thickness, gap);

        // bottom-left
        shapeRenderer.rect(x,     y, gap, thickness);
        shapeRenderer.rect(x,     y, thickness, gap);

        // bottom-right
        shapeRenderer.rect(x2 - gap, y, gap, thickness);
        shapeRenderer.rect(x2 - thickness, y, thickness, gap);

    }

    public float getCornerBracketGapRatio() {
        return cornerBracketGapRatio;
    }

    public void setCornerBracketGapRatio(float cornerBracketGapRatio) {
        this.cornerBracketGapRatio = cornerBracketGapRatio;
    }

    public float getCornerBracketThickness() {
        return cornerBracketThickness;
    }

    public void setCornerBracketThickness(float cornerBracketThickness) {
        this.cornerBracketThickness = cornerBracketThickness;
    }

}
