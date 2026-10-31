package core.entities.unit;

import arcane.utils.Array;

import static core.app.Vars.*;

public class UnitManager {
    public static UnitManager botManager;

    private float tileSize = world.getTileSize();
    public final float DEFAULT_UNIT_SIZE = tileSize * 4f;

    private Array<Unit> units = new Array<>(false, 1024);

    public Unit spawnUnit(float x, float y, float direction) {
        return spawnUnit(null, x, y, DEFAULT_UNIT_SIZE, DEFAULT_UNIT_SIZE, direction);
    }

    public Unit spawnUnit(Unit unit, float x, float y, float width, float height, float direction) {
        if (unit == null) unit = new Unit();
        unit.setPos(x, y);
        unit.setSize(width, height);
        unit.setDirection(direction);

        addUnit(unit);
        return unit;
    }

    public void unitAimTo(Unit unit) {

    }

    public void addUnit(Unit unit) {
        if (unit != null && !units.contains(unit, true)) {
            units.add(unit);
        }
    }

    public Unit getUnit(Unit unit) {
        return units.contains(unit, true) ? unit : null;
    }

    public Array<Unit> getUnits() {
        return units;
    }

    public void removeUnit(Unit unit) {
        units.removeValue(unit, true);
    }

}
