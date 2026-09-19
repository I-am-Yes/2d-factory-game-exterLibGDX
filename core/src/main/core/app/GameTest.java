package core.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanManager;
import core.event.GameEvent;
import core.system.GameSysCycle;
import data.map.asset.AssetType;
import data.map.asset.BuildingType;
import data.map.asset.FloorType;

import java.util.Random;

public class GameTest implements GameSysCycle {

    private final GameContext context;

    public GameTest(GameContext context) {
        this.context = context;

        PlanManager planManager = new PlanManager(context.world);
        PlanBuilder<AssetType> planBuilder = new PlanBuilder<>();

        float divideTest = 1f;
        //testers
        PlanBuilder<AssetType> planBuilderTest1 = new PlanBuilder<>();
        Random random = new Random();
        for (int i = 0; i < context.world.getWorldWidth() / divideTest; i++) {
            for (int j = 0; j < context.world.getWorldHeight() / divideTest; j++) {
                if (random.nextBoolean()) {
                    planBuilderTest1.addPlan(i, j, BuildingType.HAZARD_BLOCK);
                } else {
                    planBuilderTest1.addPlan(i, j, FloorType.MARBLE);
                }
            }
        }
//        GameEvent.PlanBuilderRequest.fire(planBuilderTest1);

//        planManager.addPlanToRenderQueue(planBuilder);


//        Gdx.app.log(
//            "Graphics",
//            "Vendor: " + Gdx.gl.glGetString(GL20.GL_VENDOR)
//        );
//
//        Gdx.app.log(
//            "Graphics",
//            "Renderer: " + Gdx.gl.glGetString(GL20.GL_RENDERER)
//        );
//
//        Gdx.app.log(
//            "Graphics",
//            "Version: " + Gdx.gl.glGetString(GL20.GL_VERSION)
//        );
    }

    @Override
    public void update(float delta) {

    }

}
