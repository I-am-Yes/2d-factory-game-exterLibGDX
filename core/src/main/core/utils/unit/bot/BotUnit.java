package core.utils.unit.bot;

import core.utils.unit.Unit;
import core.utils.unit.UnitType;

import static core.app.Vars.*;

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
        this.region = assets.getRegion(UnitType.BOT_UNIT);
    }
}
