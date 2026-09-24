package core.machine.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import core.assets.AssetsHandler;
import core.machine.state.ItemType;

public class RenderManager {
    public static final float VISUAL_SMOOTHNESS = 20f;
    public static final boolean SMOOTH_ANI = true;

    private final Array<IntakeVisual> intakes = new Array<>(false, 16);

    public void addIntake(ItemType type, float startX, float startY,
                          float targetX, float targetY, float speed
    ) {
        intakes.add(new IntakeVisual(type, startX, startY, targetX, targetY, speed));
    }

    public void update(float gameDelta) {
        if (gameDelta <= 0f) return;

        for (int i = intakes.size - 1; i >= 0; i--) {
            IntakeVisual visual = intakes.get(i);
            float dx = visual.targetX - visual.visualX;
            float dy = visual.targetY - visual.visualY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float move = visual.speed * gameDelta;

            if (distance <= move || move <= 0f) {
                visual.visualX = visual.targetX;
                visual.visualY = visual.targetY;
                intakes.removeIndex(i);
                continue;
            }

            visual.visualX += dx / distance * move;
            visual.visualY += dy / distance * move;
        }
    }

    public void drawBatch(SpriteBatch batch, ItemRender itemRender, AssetsHandler assets) {
        for (IntakeVisual visual : intakes) {
            itemRender.drawBatch(batch, visual.type, visual.visualX, visual.visualY, assets);
        }
    }

    private static final class IntakeVisual {
        final ItemType type;
        float visualX, visualY;
        final float targetX, targetY;
        final float speed;

        IntakeVisual(ItemType type, float startX, float startY,
                     float targetX, float targetY, float speed) {
            this.type = type;
            this.visualX = startX;
            this.visualY = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.speed = speed;
        }
    }
}
