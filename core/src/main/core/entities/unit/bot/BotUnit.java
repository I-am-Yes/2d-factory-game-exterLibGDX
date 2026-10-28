package core.entities.unit.bot;

import core.entities.unit.Unit;
import core.entities.unit.UnitType;

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
        this.region = assets.getRegion(UnitType.BOT_UNIT);
    }
}
