package core.app.extras.test;

import arcane.ApplicationListener;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanManager;
import core.event.GameEvent;
import data.map.asset.AssetType;
import data.map.asset.BuildingType;

public class GameTest implements ApplicationListener {

    public static final GameTest instance = new GameTest();

    public GameTest() {

        PlanManager planManager = new PlanManager();
        PlanBuilder<AssetType> planBuilder = new PlanBuilder<>();

        float divideTest = 1f;
        //testers
//        PlanBuilder<AssetType> planBuilderTest1 = new PlanBuilder<>();
//        Random random = new Random();
//        for (int i = 0; i < context.world.getWorldWidth() / divideTest; i++) {
//            for (int j = 0; j < context.world.getWorldHeight() / divideTest; j++) {
//                if (random.nextBoolean()) {
//                    planBuilderTest1.addPlan(i, j, BuildingType.HAZARD_BLOCK);
//                } else {
//                    planBuilderTest1.addPlan(i, j, FloorType.MARBLE);
//                }
//            }
//        }
//        GameEvent.PlanBuilderRequest.fire(planBuilderTest1);

//        planManager.addPlanToRenderQueue(planBuilder);


        int amount = 100;
        for (int y = 0; y < amount; y++) {
            GameEvent.BlockPlaceRequest.fire(0, y, BuildingType.CREATIVE_SOURCE);

            for (int x = 1; x < amount; x++) {
                GameEvent.BlockPlaceRequest.fire(x, y, BuildingType.CONVEYOR_BELT_2);}

            GameEvent.BlockPlaceRequest.fire(amount, y, BuildingType.STORAGE_CHEST_2);
        }


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
    public void init() {
    }

    @Override
    public void update() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
