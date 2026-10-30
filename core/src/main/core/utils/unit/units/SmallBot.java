package core.utils.unit.units;

import core.utils.unit.bot.BotUnit;

public class SmallBot extends BotUnit {
    public SmallBot() {
        super(4f);
        setSize(DEFAULT_UNIT_SIZE * 0.5f, DEFAULT_UNIT_SIZE * 0.5f);
    }
}
