package core.utils.unit.bot;

import com.badlogic.gdx.graphics.g2d.Sprite;
import core.utils.unit.Unit;
import core.utils.unit.UnitType;

public class BotUnit extends Unit {
    public static final float DEFAULT_SPEED_MULT = 2f;

    public BotUnit() {
        this(DEFAULT_SPEED_MULT);
    }

    protected BotUnit(float speedMult) {
        super();
        setSpeedMult(speedMult);
        setName(UnitType.BOT_UNIT.getName());
        setSize(DEFAULT_UNIT_SIZE, DEFAULT_UNIT_SIZE);

        this.sprite = new Sprite(UnitType.BOT_UNIT.getRegion());
    }
}
