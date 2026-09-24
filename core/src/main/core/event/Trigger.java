package core.event;

public enum Trigger {
    update,       // each logic() tick — Main.logic()
    beforeDraw,   // start of draw() — optional
    afterDraw     // end of draw() — optional
}
