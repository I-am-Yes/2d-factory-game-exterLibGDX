package core.machine.render;

import com.badlogic.gdx.utils.Array;
import core.app.cores.Time;
import core.machine.state.ItemType;
import core.machine.utils.Item;
import core.utils.Artist;

public class RenderManager {
    public static final float VISUAL_SMOOTHNESS = 20f;
    public static final boolean SMOOTH_ANI = true;

    private final Array<IntakeVisual> intakes = new Array<>(false, 16);

    public void addIntake(ItemType type, float startX, float startY,
                          float targetX, float targetY, float speed
    ) {
        intakes.add(new IntakeVisual(type, startX, startY, targetX, targetY, speed));
    }

    public void update() {
        for (int i = intakes.size - 1; i >= 0; i--) {
            IntakeVisual visual = intakes.get(i);
            float dx = visual.targetX - visual.visualX;
            float dy = visual.targetY - visual.visualY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float move = visual.speed * Time.gameDelta();

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


    public void drawBatch(Item item) {
        for (IntakeVisual visual : intakes) {
            Artist.DrawItem(item, visual.visualX, visual.visualY);

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
