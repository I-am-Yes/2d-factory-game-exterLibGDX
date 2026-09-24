package data.debug;

// Only for testing and debugging purpose...

// Who would think of a debugger for a debug method? Debugging Debug Debugger!!

public class testDebug {

    public static void main(String[] args) {
        DebugConfig debugConfig = DebugConfig.createDefaultConfig();

        //System.out.println("Activated: RENDER mode!");
        //debugConfig.enable(DebugType.RENDER);

        System.out.printf("active modes: %s%n", debugConfig.getEnabledModes());
        System.out.printf("inactive modes: %s%n", debugConfig.getDisabledModes());

        System.out.printf("is RENDER active: %s%n", debugConfig.isEnabled(DebugType.RENDER));

        System.out.printf("All Debug modes: %s%n", debugConfig.getAllDebugModes());


    }
}
