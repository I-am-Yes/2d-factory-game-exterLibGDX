package core.utils.unit;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import arcane.Time;
import arcane.math.MathUtils;

import static core.app.Vars.*;

public class Unit {

    public String name;
    public TextureRegion region;

    private float health;
    private float x, y;
    private float direction;
    private float width, height;

    private float speedMult = 2f;
    private float resizeMult = 2f;
    private float moveSpeed = world.getTileSize() * speedMult;
    private float resizeSpeed = world.getTileSize() * resizeMult;
    private final float rotateSpeed = 180f; // degrees per second

    public final float DEFAULT_UNIT_SIZE = world.getTileSize() * 4f;

    private final HitBox hitBox = new HitBox(this);

    public Unit() {}

    public Unit(float x, float y, float width, float height, float direction) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = direction;
    }

    public void moveTo(float x, float y) {
        float dx = x - this.x;
        float dy = y - this.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float step = moveSpeed * Time.UPDATE_INTERVAL;

        if (distance <= step) {
            setPos(x, y);
            return;
        }

        this.x += dx / distance * step;
        this.y += dy / distance * step;
    }

    public void rotateTo(float degree) {
        // Shortest signed turn, between -180 and 180 degrees.
        float difference = ((degree - direction) % 360f + 540f) % 360f - 180f;
        float step = rotateSpeed * Time.UPDATE_INTERVAL;

        direction += MathUtils.clamp(difference, -step, step);
        direction = (direction % 360f + 360f) % 360f;
    }

    public void widthTo(float width) {
        float step = resizeSpeed * Time.UPDATE_INTERVAL;
        this.width += MathUtils.clamp(width - this.width, -step, step);
    }

    public void heightTo(float height) {
        float step = resizeSpeed * Time.UPDATE_INTERVAL;
        this.height += MathUtils.clamp(height - this.height, -step, step);
    }

    public void sizeTo(float width, float height) {
        widthTo(width);
        heightTo(height);
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public void setWidth(float width) {
        this.width = width;
    }
    public void setHeight(float height) {
        this.height = height;
    }

    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    public void setSpeedMult(float speedMult) {
        this.speedMult = speedMult;
        this.moveSpeed = world.getTileSize() * speedMult;
    }

    public void setName(String name) {
        this.name = name;
    }
    public float getHealth() {
        return health;
    }
    public float getDirection() {
        return direction;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }

    public HitBox getHitBox() {
        return hitBox;
    }
}
