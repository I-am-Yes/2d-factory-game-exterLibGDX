package Data;

import java.util.EnumSet;

public class DebugData {

    public enum DebugType  {
        GAME,
        PLAYER,
        RENDER,
        INPUT,

    }

    private static final EnumSet<DebugType> activeModes = EnumSet.noneOf(DebugType.class);

    public static EnumSet<DebugType> getActiveModes() {
        return activeModes;
    }
    public static EnumSet<DebugType> getInactiveModes() {
        return EnumSet.complementOf(activeModes);
    }

    public static void enable(DebugType type) {
        activeModes.add(type);
    }

    public static void disable(DebugType type) {
        activeModes.remove(type);
    }

    public static boolean isActive(DebugType type) {
        return activeModes.contains(type);
    }

    public static EnumSet<DebugType> allDebugModes() {
        return EnumSet.allOf(DebugType.class);
    }


    // Un-comment this block to test above functions
//    public static void main(String[] args) {
//
//
//        System.out.println("Activated: RENDER mode!");
//        DebugData.enable(DebugType.RENDER);
//
//        System.out.printf("active modes: %s%n", getActiveModes());
//        System.out.printf("inactive modes: %s%n", getInactiveModes());
//
//        System.out.printf("is RENDER active: %s%n", isActive(DebugType.RENDER));
//
//        System.out.printf("All Debug modes: %s%n", allDebugModes());
//
//
//    }

}
