package core.app;

import arcane.input.*;
import core.app.extras.*;
import core.assets.*;
import core.client.*;
import core.entities.plan.*;
import core.world.*;
import core.assets.map.*;
import ui.*;

public class Vars {

    ////hmm
    public static final int MAX_FACTORY_COUNT = Integer.MAX_VALUE;
    public static final float ITEM_SIZE = 0.8f;

    public static MapConfig mapConfig;
    public static World world;

    public static AssetsHandler assets;

    public static UiInputGate uiInputGate;

    public static Player player;
    public static CameraControl cameraControl;

    public static InterfaceSystem ui;

    public static PlanManager planManager = new PlanManager();

    public static GameStart start;
    public static LoadingScreen loadingScreen;

}
