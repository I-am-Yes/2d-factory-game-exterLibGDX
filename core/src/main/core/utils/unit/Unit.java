package core.utils.unit;

import com.badlogic.gdx.graphics.g2d.Sprite;

import arcane.Time;
import arcane.math.MathUtils;

import static core.app.Vars.*;

/** A unit with a position, size, direction, and sprite. */
public class Unit {

    public String name;
    public Sprite sprite;

    private float health;
    private float x, y;
    private float direction;
    private float width, height;

    private float speedMult = 2f;
    private float resizeMult = 2f;
    private float moveSpeed = world.getTileSize() * speedMult;
    public float resizeSpeed = world.getTileSize() * resizeMult;
    public float rotateSpeed = 30f; // degrees per second

    //Smoothing properties
    private static final float ACCEL = 40f;   // how fast you speed up
    private static final float FRICTION = 25f; // how fast you slow down
    private float currentMoveSpeed;
    private float currentRotateSpeed;

    public final float DEFAULT_UNIT_SIZE = world.getTileSize() * 4f;

    private final HitBox hitBox = new HitBox(this);

    public Unit() {}

    /**
     * Smoothly changes a speed toward its target.
     *
     * @param current current linear or angular speed
     * @param target desired speed in the same units as {@code current}
     * @return the eased speed for this step
     */
    private float smoothVelocity(float current, float target) {
        float rate = Math.abs(target) < Math.abs(current) ? FRICTION : ACCEL;

        float alpha = Math.min(1f, rate * Time.UPDATE_INTERVAL);
        float result = MathUtils.lerp(current, target, alpha);

        return Math.abs(result) < 0.001f ? 0f : result;
    }

    /**
     * Smoothly moves to a position and rotates to a direction.
     *
     * @param x target bottom-left x coordinate in world units
     * @param y target bottom-left y coordinate in world units
     * @param direction target facing angle in degrees
     */
    public void moveAndDirTo(float x, float y, float direction) {
        rotateTo(direction);

        float difference =
            ((direction - this.direction) % 360f + 540f) % 360f - 180f;

        if (Math.abs(difference) <= 5f) {
            moveTo(x, y);
        }
    }

    /**
     * Smoothly moves toward a position.
     *
     * @param x target bottom-left x coordinate in world units
     * @param y target bottom-left y coordinate in world units
     */
    public void moveTo(float x, float y) {
        float dx = x - this.x;
        float dy = y - this.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        currentMoveSpeed = smoothVelocity(
            currentMoveSpeed,
            Math.min(moveSpeed, distance * FRICTION)
        );

        float step = currentMoveSpeed * Time.UPDATE_INTERVAL;

        if (distance <= Math.max(step, 0.001f)) {
            setPos(x, y);
            currentMoveSpeed = 0f;
            return;
        }

        translate(dx / distance * step, dy / distance * step);
    }

    /**
     * Moves in the given direction in degrees.
     *
     * @param degrees movement heading in degrees, positive counterclockwise
     */
    public void moveDir(float degrees) {
        currentMoveSpeed = smoothVelocity(currentMoveSpeed, moveSpeed);
        float step = currentMoveSpeed * Time.UPDATE_INTERVAL;

        translate(
            MathUtils.cosDeg(degrees) * step,
            MathUtils.sinDeg(degrees) * step
        );
    }

    /**
     * Smoothly turns to face a position.
     *
     * @param x target world x coordinate
     * @param y target world y coordinate
     */
    public void dirTo(float x, float y) {
        float dx = x - (this.x + width / 2f);
        float dy = y - (this.y + height / 2f);
        if (dx == 0f && dy == 0f) return;
        float degrees = (float) Math.toDegrees(Math.atan2(dy, dx));
        rotateTo(degrees);
    }

    /**
     * Smoothly turns toward a target angle in degrees.
     *
     * @param degree target facing angle in degrees
     */
    public void rotateTo(float degree) {
        float difference =
            ((degree - direction) % 360f + 540f) % 360f - 180f;
        float distance = Math.abs(difference);

        currentRotateSpeed = smoothVelocity(
            currentRotateSpeed,
            Math.min(rotateSpeed, distance * FRICTION)
        );

        float step = currentRotateSpeed * Time.UPDATE_INTERVAL;

        if (distance <= Math.max(step, 0.01f)) {
            rotateBy(difference);
            currentRotateSpeed = 0f;
            return;
        }

        rotateBy(Math.signum(difference) * step);
    }

    /**
     * Smoothly turns using input from -1 to 1; zero slows turning.
     *
     * @param amount turning input clamped to {@code [-1, 1]}; zero applies friction
     */
    public void turn(float amount) {
        currentRotateSpeed = smoothVelocity(
            currentRotateSpeed,
            MathUtils.clamp(amount, -1f, 1f) * rotateSpeed
        );

        rotateBy(currentRotateSpeed * Time.UPDATE_INTERVAL);
    }

    /**
     * Gradually changes the width.
     *
     * @param width target width in world units
     */
    public void widthTo(float width) {
        float step = resizeSpeed * Time.UPDATE_INTERVAL;
        setWidth(this.width + MathUtils.clamp(width - this.width, -step, step));
    }

    /**
     * Gradually changes the height.
     *
     * @param height target height in world units
     */
    public void heightTo(float height) {
        float step = resizeSpeed * Time.UPDATE_INTERVAL;
        setHeight(this.height + MathUtils.clamp(height - this.height, -step, step));
    }

    /**
     * Gradually changes the width and height.
     *
     * @param width target width in world units
     * @param height target height in world units
     */
    public void sizeTo(float width, float height) {
        widthTo(width);
        heightTo(height);
    }

    /**
     * Immediately rotates by the given angle in degrees.
     *
     * @param degrees angle to add; positive is counterclockwise
     */
    public void rotateBy(float degrees) {
        direction += degrees;

        if (sprite != null) {
            sprite.rotate(degrees);
        }
    }

    /**
     * Moves the unit and its sprite by the given offset.
     *
     * @param dx horizontal displacement in world units
     * @param dy vertical displacement in world units
     */
    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;

        if (sprite != null) {
            sprite.translate(dx, dy);
        }
    }

    /**
     * Sets the position of the unit and its sprite.
     *
     * @param x new bottom-left x coordinate in world units
     * @param y new bottom-left y coordinate in world units
     */
    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;

        if (sprite != null) {
            sprite.setPosition(x, y);
        }
    }

    /**
     * Sets the stored direction and rotates the sprite by the given angle.
     *
     * @param direction new stored facing angle in degrees
     */
    public void setDirection(float direction) {
        this.direction = direction;
        if (sprite != null) {
            sprite.rotate(direction);
        }
    }

    /**
     * Sets the width of the unit and its sprite.
     *
     * @param width new width in world units
     */
    public void setWidth(float width) {
        this.width = width;
        setSize(width, this.height);
    }
    /**
     * Sets the height of the unit and its sprite.
     *
     * @param height new height in world units
     */
    public void setHeight(float height) {
        this.height = height;
        setSize(this.width, height);
    }

    /**
     * Sets the size of the unit and its sprite.
     *
     * @param width new width in world units
     * @param height new height in world units
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;

        if (sprite != null) {
            sprite.setSize(width, height);
            sprite.setOriginCenter();
        }
    }

    /**
     * Sets the movement speed multiplier.
     *
     * @param speedMult tile-size multiplier for movement speed
     */
    public void setSpeedMult(float speedMult) {
        this.speedMult = speedMult;
        this.moveSpeed = world.getTileSize() * speedMult;
    }

    /**
     * Sets the unit's name.
     *
     * @param name display name
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Returns the unit's health.
     *
     * @return current health
     */
    public float getHealth() {
        return health;
    }
    /**
     * Returns the facing angle in degrees.
     *
     * @return facing angle in degrees
     */
    public float getDirection() {
        return direction;
    }

    /**
     * Returns the x position.
     *
     * @return x coordinate in world units
     */
    public float getX() {
        return x;
    }
    /**
     * Returns the y position.
     *
     * @return y coordinate in world units
     */
    public float getY() {
        return y;
    }
    /**
     * Returns the width.
     *
     * @return width in world units
     */
    public float getWidth() {
        return width;
    }
    /**
     * Returns the height.
     *
     * @return height in world units
     */
    public float getHeight() {
        return height;
    }

    /**
     * Returns the unit's hitbox.
     *
     * @return the hitbox owned by this unit
     */
    public HitBox getHitBox() {
        return hitBox;
    }
}
