package core.app.vars;

import core.UiInputGate;
import core.controller.camera.CameraController;
import core.player.Player;
import core.world.World;
import data.map.MapConfig;
import ui.InterfaceSystem;

public class Vars {

    ////hmm
    public static final int MAX_FACTORY_COUNT = Integer.MAX_VALUE;
    public static final float ITEM_SIZE = 0.8f;

    public static MapConfig mapConfig;
    public static World world;

    public static UiInputGate uiInputGate;

    public static Player player;
    public static CameraController cameraControl;

    public static InterfaceSystem ui;

}
