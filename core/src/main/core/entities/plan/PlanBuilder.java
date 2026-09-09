package core.entities.plan;

import data.map.asset.AssetType;
import data.map.asset.GhostType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;

public class PlanBuilder<T extends AssetType> {

    private final Queue<PlanEntity<T>> planQueue = new Queue<>();

    public void addPlan(int x, int y, T currentPlan) {
        planQueue.addLast(
            new PlanEntity<>(x, y, GhostType.translateToGhost(currentPlan)));
    }

    public void addPlan(float x, float y, T currentPlan) {
        addPlan((int) x, (int) y, currentPlan);
    }

    public void addPlan(Vector2 vector2, T currentPlan) {
        addPlan((int) vector2.x, (int) vector2.y, currentPlan);
    }

    public Queue<PlanEntity<T>> getPlanQueue() {
        return planQueue;
    }

    public void clearPlanQueue() {
        planQueue.clear();
    }

    public boolean isPlanQueueEmpty() {
        return planQueue.isEmpty();
    }

    public PlanEntity<T> getPlanEntityAt(int index) {
        return planQueue.get(index);
    }

    public void removePlanEntityAt(int index) {
        planQueue.removeIndex(index);
    }

}
