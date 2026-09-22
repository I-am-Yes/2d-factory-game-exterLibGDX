package core.machine;

import core.machine.category.ExposeInfo;
import core.machine.state.Direction;

//TODO: implement exposable this block content
public class Block {

    public String name;
    public float size;

    public float health;
    public float x;
    public float y;
    public Direction direction;

    /** The size of the multi-block in the X and Y directions. */
    public short multiX, multiY;

    public boolean multiBlock;
    public boolean update;
    public boolean tickUpdate;
    public boolean rotatable = true;
    public boolean breakable = true;

    public Block() {}

    public Block(int tileX, int tileY, Direction direction) {
        this.x = tileX;
        this.y = tileY;
        this.direction = direction;
    }

    public Block init(int tileX, int tileY, Direction direction) {
        return new Block(tileX, tileY, direction);
    }

    public Machine createMachine(int tileX, int tileY, Direction direction) {
        throw new UnsupportedOperationException(
            getClass().getSimpleName() + " cannot create a machine"
        );
    }

    public boolean needUpdate() {
        return update;
    }

    public boolean needTickUpdate() {
        return tickUpdate;
    }

    public boolean isRotatable() {
        return rotatable;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public int getWidth() {
        return multiBlock ? multiX : (int) size;
    }

    public int getHeight() {
        return multiBlock ? multiY : (int) size;
    }

    public float getHealth() {
        return health;
    }

    static class MultiBlock extends Block {
        public final int width;
        public final int height;
        public MultiBlock(int width, int height) {
            ////hmmmm... shouldn't it be able to negative?
//            if (width <= 0 || height <= 0) {
//                throw new IllegalArgumentException("Block size must be positive");
//            }
            this.width = width;
            this.height = height;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }
    }


}
