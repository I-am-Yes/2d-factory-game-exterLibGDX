package core.app.extras.test;

import core.app.cores.AppListener;
import core.entities.plan.PlanBuilder;
import core.entities.plan.PlanManager;
import core.event.events.GameEvent;
import core.app.cores.GameSysCycle;
import data.map.asset.AssetType;
import data.map.asset.BuildingType;

import static core.app.Vars.*;

public class GameTest implements AppListener, GameSysCycle {

    public GameTest() {

        PlanManager planManager = new PlanManager(world);
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
        AppListener.super.init();
    }

    @Override
    public void update() {
        AppListener.super.update();
    }

    @Override
    public void create() {
        AppListener.super.create();
    }

    @Override
    public void render() {
        AppListener.super.render();
    }

    @Override
    public void resize(int width, int height) {
        AppListener.super.resize(width, height);
    }

    @Override
    public void pause() {
        AppListener.super.pause();
    }

    @Override
    public void resume() {
        AppListener.super.resume();
    }

    @Override
    public void dispose() {
        AppListener.super.dispose();
    }
}
