package core.system.context;

import core.debug.*;
import core.debug.debuggers.*;

public class DebugContext {

    public Debug debug;
    public CameraDebugger cameraDebugger;
    public EventsDebugger eventsDebugger;
    public MapGenDebugger mapGenDebugger;
    public PerformanceDebugger performanceDebugger;
    public RenderDebugger renderDebugger;

    public DebugContext(Debug debug, CameraDebugger cameraDebugger, EventsDebugger eventsDebugger, MapGenDebugger mapGenDebugger, PerformanceDebugger performanceDebugger, RenderDebugger renderDebugger) {
        this.debug = debug;
        this.cameraDebugger = cameraDebugger;
        this.eventsDebugger = eventsDebugger;
        this.mapGenDebugger = mapGenDebugger;
        this.performanceDebugger = performanceDebugger;
        this.renderDebugger = renderDebugger;
    }

}
