package data.debug;

import java.util.EnumSet;

public class DebugConfig {


    public EnumSet<DebugType> enabled = java.util.EnumSet.noneOf(DebugType.class);

    public static DebugConfig createDefaultConfig() {
        DebugConfig config = new DebugConfig();
        // default config for debug mode
        //config.enabled.add(DebugType.INPUT);
        return config;

    }

    public void enable(DebugType debugType) {
        enabled.add(debugType);
    }

    public void disable(DebugType debugType) {
        enabled.remove(debugType);
    }

    public boolean isEnabled(DebugType debugType) {
        return enabled.contains(debugType);
    }

    public EnumSet<DebugType> getEnabledModes() {
        return EnumSet.copyOf(enabled);
    }

    public EnumSet<DebugType> getDisabledModes() {
        return EnumSet.complementOf(enabled);
    }

    public EnumSet<DebugType> getAllDebugModes() {
        return EnumSet.allOf(DebugType.class);
    }

    public DebugType[] getAllDebugModeNames() {
        return DebugType.values();
    }

    public int getDebugModeCount() {
        return DebugType.values().length;
    }

    public boolean isAllDebugDisabled() {
        return getEnabledModes().isEmpty();
    }

    public boolean isAllDebugEnabled() {
        return getEnabledModes().containsAll(getAllDebugModes());
    }

}
