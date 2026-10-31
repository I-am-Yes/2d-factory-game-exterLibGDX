package core.client.player.mechanic;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import arcane.graphics.*;
import arcane.utils.Array;
import core.utils.unit.Unit;

import static arcane.Cores.*;
import static core.app.Vars.*;

public final class HitBoxCursor {
    private Unit hoveredUnit;

    public void update() {
        hoveredUnit = null;

        if (uiInputGate != null && uiInputGate.isPointerOverGUI()) {
            return;
        }

        var mouse = input.getMouseWorldPos(viewport);
        float mouseX = mouse.x;
        float mouseY = mouse.y;

        hoveredUnit = findHovered(
            botManager.getBots(), mouseX, mouseY
        );

        if (hoveredUnit == null) {
            hoveredUnit = findHovered(
                botManager.getUnits(), mouseX, mouseY
            );
        }

        drawShape();
    }

    private Unit findHovered(
        Array<? extends Unit> units, float mouseX, float mouseY) {

        for (int i = units.size - 1; i >= 0; i--) {
            Unit unit = units.get(i);

            if (unit != null && unit.sprite != null
                && unit.getHitBox().contains(mouseX, mouseY)) {
                return unit;
            }
        }

        return null;
    }

    public void drawShape() {
        if (hoveredUnit == null) return;

        Artist.flush();
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);

        try {
            shape.setColor(Color.YELLOW);
            shape.polygon(hoveredUnit.getHitBox().getVertices());
        } finally {
            shape.end();
        }
    }

    public Unit getHoveredUnit() {
        return hoveredUnit;
    }
}
